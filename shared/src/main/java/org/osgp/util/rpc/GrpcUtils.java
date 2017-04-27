package org.osgp.util.rpc;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.security.auth.x500.X500Principal;

import io.grpc.ManagedChannel;
import io.grpc.ServerBuilder;
import io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.NegotiationType;
import io.grpc.netty.NettyChannelBuilder;
import io.grpc.netty.NettyServerBuilder;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslProvider;

public class GrpcUtils {
	public static final String TEST_SERVER_HOST = "foo.test.google.fr";

	/**
	 * Creates a new {@link InetSocketAddress} that overrides the host with
	 * {@link #TEST_SERVER_HOST}.
	 */
	public static InetSocketAddress testServerAddress(String host, int port) {
		try {
			InetAddress inetAddress = InetAddress.getByName(host);
			inetAddress = InetAddress.getByAddress(TEST_SERVER_HOST, inetAddress.getAddress());
			return new InetSocketAddress(inetAddress, port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a new {@link InetSocketAddress} on localhost that overrides the
	 * host with {@link #TEST_SERVER_HOST}.
	 */
	public static InetSocketAddress testServerAddress(int port) {
		try {
			InetAddress inetAddress = InetAddress.getByName("localhost");
			inetAddress = InetAddress.getByAddress(TEST_SERVER_HOST, inetAddress.getAddress());
			return new InetSocketAddress(inetAddress, port);
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Returns the ciphers preferred to use during tests. They may be chosen
	 * because they are widely available or because they are fast. There is no
	 * requirement that they provide confidentiality or integrity.
	 */
	public static List<String> preferredTestCiphers() {
		String[] ciphers;
		try {
			ciphers = SSLContext.getDefault().getDefaultSSLParameters().getCipherSuites();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException(ex);
		}
		List<String> ciphersMinusGcm = new ArrayList<String>();
		for (String cipher : ciphers) {
			// The GCM implementation in Java is _very_ slow (~1 MB/s)
			if (cipher.contains("_GCM_")) {
				continue;
			}
			ciphersMinusGcm.add(cipher);
		}
		return Collections.unmodifiableList(ciphersMinusGcm);
	}

	/**
	 * Saves a file from the classpath resources in src/main/resources/certs as
	 * a file on the filesystem.
	 *
	 * @param name
	 *            name of a file in src/main/resources/certs.
	 */
	public static File loadCert(String name) {
		try {
			InputStream in = GrpcUtils.class.getResourceAsStream("/certs/" + name);
			File tmpFile = File.createTempFile(name, "");
			tmpFile.deleteOnExit();

			BufferedWriter writer = new BufferedWriter(new FileWriter(tmpFile));
			try {
				int b;
				while ((b = in.read()) != -1) {
					writer.write(b);
				}
			} finally {
				writer.close();
			}

			return tmpFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Loads an X.509 certificate from the classpath resources in
	 * src/main/resources/certs.
	 * ../grpc/grpc-ssl-test-code/src/main/java/io/grpc/examples/helloworld/
	 * TestUtils.java
	 * 
	 * @param fileName
	 *            name of a file in src/main/resources/certs.
	 */
	public static X509Certificate loadX509Cert(String fileName) throws CertificateException, IOException {
		CertificateFactory cf = CertificateFactory.getInstance("X.509");

		InputStream in = GrpcUtils.class.getResourceAsStream("/certs/" + fileName);
		try {
			return (X509Certificate) cf.generateCertificate(in);
		} finally {
			in.close();
		}
	}

	/**
	 * Creates an SSLSocketFactory which contains {@code certChainFile} as its
	 * only root certificate.
	 */
	public static SSLSocketFactory newSslSocketFactoryForCa(File certChainFile) throws Exception {
		InputStream is = new FileInputStream(certChainFile);
		try {
			return newSslSocketFactoryForCa(is);
		} finally {
			is.close();
		}
	}

	/**
	 * Creates an SSLSocketFactory which contains {@code certChainFile} as its
	 * only root certificate.
	 */
	public static SSLSocketFactory newSslSocketFactoryForCa(InputStream certChain) throws Exception {
		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		ks.load(null, null);
		CertificateFactory cf = CertificateFactory.getInstance("X.509");
		X509Certificate cert = (X509Certificate) cf.generateCertificate(new BufferedInputStream(certChain));
		X500Principal principal = cert.getSubjectX500Principal();
		ks.setCertificateEntry(principal.getName("RFC2253"), cert);

		// Set up trust manager factory to use our key store.
		TrustManagerFactory trustManagerFactory = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		trustManagerFactory.init(ks);
		SSLContext context = SSLContext.getInstance("TLS");
		context.init(null, trustManagerFactory.getTrustManagers(), null);
		return context.getSocketFactory();
	}

	public static ServerBuilder<?> serverBuilder(int port, File serverCertChainFile, File serverPrivateKeyFile,
			X509Certificate[] serverTrustedCaCerts, SslProvider sslProvider) throws IOException {
		SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(serverCertChainFile, serverPrivateKeyFile);
		GrpcSslContexts.configure(sslContextBuilder, sslProvider);
		sslContextBuilder.trustManager(serverTrustedCaCerts).clientAuth(ClientAuth.REQUIRE);

		return NettyServerBuilder.forPort(port).sslContext(sslContextBuilder.build());
	}

	public static ServerBuilder<?> serverBuilder(int port, String serverCertChainFilename, String serverPrivateKeyFilename,
			String serverTrustedCaCertsFilename, SslProvider sslProvider) throws IOException, CertificateException {
		
		File serverCertChainFile = GrpcUtils.loadCert(serverCertChainFilename);
		File serverPrivateKeyFile = GrpcUtils.loadCert(serverPrivateKeyFilename);
		X509Certificate[] serverTrustedCaCerts = { GrpcUtils.loadX509Cert(serverTrustedCaCertsFilename) };

		SslContextBuilder sslContextBuilder = SslContextBuilder.forServer(serverCertChainFile, serverPrivateKeyFile);
		GrpcSslContexts.configure(sslContextBuilder, sslProvider);
		sslContextBuilder.trustManager(serverTrustedCaCerts).clientAuth(ClientAuth.REQUIRE);

		return NettyServerBuilder.forPort(port).sslContext(sslContextBuilder.build());
	}

	private static ManagedChannel clientChannel(int port, SslContext sslContext) throws IOException {
		return NettyChannelBuilder.forAddress("localhost", port)
				.overrideAuthority(TEST_SERVER_HOST)
				.negotiationType(NegotiationType.TLS)
				.sslContext(sslContext)
				.build();
	}

	public static ManagedChannel makeChannel(int port) throws CertificateException, IOException, SSLException {
		File clientCertChainFile = GrpcUtils.loadCert("client.pem");
		File clientPrivateKeyFile = GrpcUtils.loadCert("client.key");
		X509Certificate[] clientTrustedCaCerts = { GrpcUtils.loadX509Cert("ca.pem") };

		SslContextBuilder clientContextBuilder = GrpcSslContexts.configure(SslContextBuilder.forClient(), SslProvider.OPENSSL);

		return GrpcUtils.clientChannel(port, clientContextBuilder.keyManager(clientCertChainFile, clientPrivateKeyFile)
				.trustManager(clientTrustedCaCerts).build());
	}		


	private GrpcUtils() {
	}
}