/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.application.threads;

import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.application.services.DlmsDevice;
import org.osgp.pa.dlms.application.services.DomainHelperService;

public class RecoverKeyProcess implements Runnable {

//    private static final Logger LOGGER = LoggerFactory.getLogger(RecoverKeyProcess.class);

    private final DomainHelperService domainHelperService;

    private final DlmsDao dlmsDeviceDao;

    private final int responseTimeout;
    //
    private final int logicalDeviceAddress;

    private final int clientAccessPoint;

    private String deviceIdentification;

    private DlmsDevice dlmsDevice;

    private String ipAddress;

    public RecoverKeyProcess(final DomainHelperService domainHelperService,
            final DlmsDao dlmsDeviceDao, final int responseTimeout, final int logicalDeviceAddress,
            final int clientAccessPoint) {
        this.domainHelperService = domainHelperService;
        this.dlmsDeviceDao = dlmsDeviceDao;
        this.responseTimeout = responseTimeout;
        this.logicalDeviceAddress = logicalDeviceAddress;
        this.clientAccessPoint = clientAccessPoint;
    }

    public void setDeviceIdentification(final String deviceIdentification) {
        this.deviceIdentification = deviceIdentification;
    }

    public void setIpAddress(final String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void run() {
//        this.checkState();

//        LOGGER.info("Attempting key recovery for dlmsDevice {"+ this.deviceIdentification + "}");
//
//        this.initDevice();
//        if (!this.dlmsDevice.hasNewSecurityKey()) {
//            return;
//        }
//
//        if (this.canConnect()) {
//            this.promoteInvalidKey();
//        }
    }

	@Override
	public String toString() {
		return "RecoverKeyProcess [domainHelperService=" + domainHelperService + ", dlmsDeviceDao=" + dlmsDeviceDao
				+ ", responseTimeout=" + responseTimeout + ", logicalDeviceAddress=" + logicalDeviceAddress
				+ ", clientAccessPoint=" + clientAccessPoint + ", deviceIdentification=" + deviceIdentification
				+ ", dlmsDevice=" + dlmsDevice + ", ipAddress=" + ipAddress + "]";
	}

    
//    private void initDevice() {
//        try {
//            this.dlmsDevice = this.domainHelperService.findDlmsDevice(this.deviceIdentification, this.ipAddress);
//        } catch (final ProtocolAdapterException | FunctionalException e) {
//            // Thread can not recover from these exceptions.
//            throw new RecoverKeyException(e.getMessage(), e);
//        }
//
//        if (this.dlmsDevice == null) {
//            throw new IllegalArgumentException("Device " + this.deviceIdentification + " not found.");
//        }
//    }

//    private void checkState() {
//        if (this.deviceIdentification == null) {
//            throw new IllegalStateException("DeviceIdentification not set.");
//        }//    /**
//  * Create a connection with the dlmsDevice.
//  *
//  * @return The connection.
//  * @throws IOException
//  *             When there are problems in connecting to or communicating
//  *             with the dlmsDevice.
//  */

//        if (this.ipAddress == null) {
//            throw new IllegalStateException("IP address not set.");
//        }
//    }
//
//    private boolean canConnect() {
//        DlmsConnection connection = null;
//        try {
//            connection = this.createConnection();
//            return true;
//        } catch (final Exception e) {
//            LOGGER.warning("Connection exception: {" + e.getMessage() + "}");
//            return false;
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (final IOException e) {
//                    LOGGER.warning("Connection exception: {"+ e.getMessage() + "}");
//                }
//            }
//        }
//    }
//
//    private void promoteInvalidKey() {
//        this.dlmsDevice.promoteInvalidKey();
//        this.dlmsDevice.save();
//    }

//    /**
//     * Create a connection with the dlmsDevice.
//     *
//     * @return The connection.
//     * @throws IOException
//     *             When there are problems in connecting to or communicating
//     *             with the dlmsDevice.
//     */
//    private DlmsConnection createConnection() throws IOException {
//        final byte[] authenticationKey = Hex.decode(this.getSecurityKey(SecurityKeyType.AUTH_KEY)
//                .getKey());
//        final byte[] encryptionKey = Hex.decode(this.getSecurityKey(SecurityKeyType.ENC_KEY).getKey());
//
//        Authentication auth = Authentication.newGmacAuthentication(authenticationKey, encryptionKey, CryptographicAlgorithm.AES_GMC_128);
//        
//        final TcpConnectionBuilder tcpConnectionBuilder = 
//                new TcpConnectionBuilder(InetAddress.getByName(this.dlmsDevice.getNetworkAddress()))
//                    .setAuthentication(auth)
//                    .setResponseTimeout(this.responseTimeout)
//                    .setLogicalDeviceId(this.logicalDeviceAddress)
//                    .setClientId(clientAccessPoint);
//
//        final Integer challengeLength = this.dlmsDevice.getDlmsDeviceMsg().getChallLen();
//        if (challengeLength != null) {
//            tcpConnectionBuilder.setChallengeLength(challengeLength);
//        }
//
//        return tcpConnectionBuilder.buildLnConnection();
//    }

//    private SecurityKeyMsg getSecurityKey(final SecurityKeyType securityKeyType) {
//        SecurityKeyMsg key = this.dlmsDevice.getNewSecurityKey(securityKeyType);
//        if (key == null) {
//            key = this.dlmsDevice.getValidSecurityKey(securityKeyType);
//        }
//        return key;
//    }
}
