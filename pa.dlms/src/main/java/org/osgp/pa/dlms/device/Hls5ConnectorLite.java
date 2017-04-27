/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.device;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Security;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.bouncycastle.util.Arrays;
import org.openmuc.jdlms.AuthenticationMechanism;
import org.openmuc.jdlms.DlmsConnection;
import org.openmuc.jdlms.SecuritySuite;
import org.openmuc.jdlms.SecuritySuite.EncryptionMechanism;
import org.openmuc.jdlms.TcpConnectionBuilder;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.EncrypterException;
import org.osgp.shared.exceptionhandling.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Hls5ConnectorLite {

	private static final Logger LOGGER = LoggerFactory.getLogger(Hls5ConnectorLite.class);

	private DlmsDevice dlmsDevice;

	private int responseTimeout = 5000;

	private static EncryptionServiceLite ENCRYPTION_SRV = new EncryptionServiceLite();

	public Hls5ConnectorLite(final DlmsDevice device) {
		this.dlmsDevice = device;
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	public DlmsConnection connect() throws TechnicalException {

		// Make sure neither device or device.getIpAddress() is null.
		this.checkDevice();
		this.checkIpAddress();

		try {
			final DlmsConnection connection = this.createConnection();
			return connection;
		} catch (final UnknownHostException e) {
			LOGGER.warn("The IP address is not found: {} " + this.dlmsDevice.getNetworkAddress());
			// Unknown IP, unrecoverable.
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
					"The IP address is not found: " + this.dlmsDevice.getNetworkAddress());
		} catch (final IOException e) {
//			throw new ConnectionException(e);
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS, 
					"can not connect to " + this.dlmsDevice.getNetworkAddress());
		} catch (final EncrypterException e) {
			LOGGER.warn(
					"decryption on security keys went wrong for device: {} " + this.dlmsDevice.getIdentification());
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS,
					"decryption on security keys went wrong for device: " + this.dlmsDevice.getIdentification());
		}
	}

	private void checkDevice() throws TechnicalException {
		if (this.dlmsDevice == null) {
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Can not connect to device, because no device is set.");
		}
		
		if (this.dlmsDevice.getAuthKey() == null || this.dlmsDevice.getEncKey() == null) {
			String msg = "auth and/or enc key of device " + this.dlmsDevice.getIdentification() + " is null";
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS, msg);
		}
	}

	private void checkIpAddress() throws TechnicalException {
		if (this.dlmsDevice.getNetworkAddress() == null) {
			throw new TechnicalException(ComponentType.PROTOCOL_DLMS, "Unable to get HLS5 connection for device "
					+ this.dlmsDevice.getIdentification() + ", because the IP address is not set.");
		}
	}

	/**
	 * Create a connection with the device.
	 *
	 * @return The connection.
	 * @throws IOException
	 *             When there are problems in connecting to or communicating
	 *             with the device.
	 * @throws TechnicalException
	 *             When there are problems reading the security and
	 *             authorisation keys.
	 * @throws EncrypterException
	 *             When there are problems decrypting the encrypted security and
	 *             authorisation keys.
	 */
	private DlmsConnection createConnection() throws IOException, TechnicalException {

		final DecryptedKeys decrKeys = decryptedKeys(dlmsDevice);

		 final SecuritySuite securitySuite = SecuritySuite.builder().setAuthenticationKey(decrKeys.authentication)
		         .setAuthenticationMechanism(AuthenticationMechanism.HLS5_GMAC)
		         .setGlobalUnicastEncryptionKey(decrKeys.encryption)
		         .setEncryptionMechanism(EncryptionMechanism.AES_GMC_128).build();
		 
		// Setup connection to device
		final TcpConnectionBuilder tcpConnectionBuilder = new TcpConnectionBuilder(
				InetAddress.getByName(this.dlmsDevice.getNetworkAddress()))
				.setSecuritySuite(securitySuite)
				.setResponseTimeout(this.responseTimeout).setLogicalDeviceId(1).setClientId(1);

		this.setOptionalValues(tcpConnectionBuilder);

		return tcpConnectionBuilder.build();
	}

	private void setOptionalValues(final TcpConnectionBuilder tcpConnectionBuilder) {
		if (this.dlmsDevice.getDlmsDeviceMsg().getPort() != 0) {
			tcpConnectionBuilder.setTcpPort(this.dlmsDevice.getDlmsDeviceMsg().getPort());
		}
		// if (this.device.getLogicalId() != null) {
		// tcpConnectionBuilder.setLogicalDeviceId(this.device.getLogicalId().intValue());
		// }

		// final Integer challengeLength = this.device.get;
		// if (challengeLength != null) {
		// tcpConnectionBuilder.setChallengeLength(challengeLength);
		// }
	}
	
	private DecryptedKeys decryptedKeys(final DlmsDevice dlmsDevice) throws TechnicalException, EncrypterException {
		// Decode the key from Hexstring to bytes
		byte[] authenticationKey = null;
		byte[] encryptionKey = null;
		try {
			final String validAuthenticationKey = this.dlmsDevice.getAuthKey();
			final String validEncryptionKey = this.dlmsDevice.getEncKey();
			authenticationKey = Hex.decodeHex(validAuthenticationKey.toCharArray());
			encryptionKey = Hex.decodeHex(validEncryptionKey.toCharArray());
		} catch (final DecoderException e) {
			throw new EncrypterException(e);
		}
		
		// Decrypt the key, discard ivBytes
		byte[] decryptedAuthentication = ENCRYPTION_SRV.decrypt(authenticationKey);
		byte[] decryptedEncryption = ENCRYPTION_SRV.decrypt(encryptionKey);
		decryptedAuthentication = Arrays.copyOfRange(decryptedAuthentication, 16, decryptedAuthentication.length);
		decryptedEncryption = Arrays.copyOfRange(decryptedEncryption, 16, decryptedEncryption.length);
		
		return new DecryptedKeys(decryptedAuthentication, decryptedEncryption);
	}
	
	//-- helper class to return tuple
	class DecryptedKeys {
		byte[] authentication;
		byte[] encryption;
		
		public DecryptedKeys(byte[] authentication, byte[] encryption) {
			super();
			this.authentication = authentication;
			this.encryption = encryption;
		}
	}

}
