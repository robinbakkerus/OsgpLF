/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.device;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.osgp.shared.exceptionhandling.EncrypterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encryption service class that offers encrypt and decrypt methods to encrypt
 * or decrypt data. You can use this service as a bean, for example by including
 * its package in component scan. This service uses a property
 * encryption.key.path that should point to a File containing the secret key.
 * When encrypting apart from this service always use {@link #getIvbytes()}.
 *
 */
public class EncryptionServiceLite {
	
    private static final Logger LOGGER = LoggerFactory.getLogger(EncryptionServiceLite.class);

    /**
     * the algorithm used to load the secret key
     */
    public static final String SECRET_KEY_SPEC = "AES";
    private static final String UNEXPECTED_EXCEPTION_DURING_ENCRYPTION = "Unexpected exception during encryption";
    private static final String UNEXPECTED_EXCEPTION_DURING_DECRYPTION = "Unexpected exception during decryption";
    private static final String UNEXPECTED_EXCEPTION_WHEN_READING_KEY = "Unexpected exception when reading key";

    /**
     * the algorithm used for reading the secret key
     */
    public static final String ALGORITHM = "AES/CBC/PKCS7PADDING";
    /**
     * the id of the provider used
     */
    public static final String PROVIDER = "BC";

//   todo via Config @Value("${encryption.key.path}")
    private String keyPath = "/my-dev/config/osgp.secret.aes";
    private SecretKey key;

    private static final byte[] IVBYTES = new byte[16];

    /**
     * for testability
     *
     * @param key
     *            A SecretKeySpec instance
     */
    protected EncryptionServiceLite(final SecretKeySpec key) {
        this.key = key;
    }

    public EncryptionServiceLite() {
        initEncryption();
    }

    /*
     * initialization of the IVBYTES used for encryption and decryption clients
     * (encryptors) have to use these ivBytes when encrypting, for example:
     * openssl enc -e -aes-128-cbc ..... -iv 000102030405060708090a0b0c0d0e0f
     */
    static {
        for (short s = 0; s < IVBYTES.length; s++) {
            IVBYTES[s] = (byte) s;
        }
    }

    private void initEncryption() {
        try {
            this.key = new SecretKeySpec(Files.readAllBytes(new File(this.keyPath).toPath()), SECRET_KEY_SPEC);
        } catch (final IOException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_WHEN_READING_KEY);
            throw new EncrypterException(UNEXPECTED_EXCEPTION_WHEN_READING_KEY, e);
        }
    }

    /**
     * Decrypts the data using the key
     */
    public byte[] decrypt(final byte[] inputData) {

        try {
        	LOGGER.debug("" + this.key.getEncoded().length);
        	LOGGER.debug(this.key.getAlgorithm());
        	LOGGER.debug(this.key.getFormat());
        	final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.DECRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
            return cipher.doFinal(inputData);
        } catch (final NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException
                | IllegalBlockSizeException | BadPaddingException | NoSuchProviderException
                | InvalidAlgorithmParameterException ex) {
            LOGGER.error(UNEXPECTED_EXCEPTION_DURING_DECRYPTION);
            throw new EncrypterException("Unexpected exception during decryption!", ex);
        }
    }

    /**
     * Encrypts the data using the key
     */
    public byte[] encrypt(final byte[] inputData) {
        try {
            final Cipher cipher = Cipher.getInstance(ALGORITHM, PROVIDER);
            cipher.init(Cipher.ENCRYPT_MODE, this.key, new IvParameterSpec(IVBYTES));
            return cipher.doFinal(inputData);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException
                | BadPaddingException | NoSuchProviderException | InvalidAlgorithmParameterException e) {
            LOGGER.error(UNEXPECTED_EXCEPTION_DURING_ENCRYPTION);
            throw new EncrypterException("Unexpected exception during encryption!", e);
        }
    }

    public static byte[] getIvbytes() {
        return Arrays.copyOf(IVBYTES, IVBYTES.length);
    }

}
