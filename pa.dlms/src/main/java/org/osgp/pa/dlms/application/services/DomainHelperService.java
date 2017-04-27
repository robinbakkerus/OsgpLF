/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.application.services;

import org.osgp.pa.dlms.application.dao.DlmsDao;
import org.osgp.pa.dlms.exceptions.ProtocolAdapterException;
import org.osgp.shared.exceptionhandling.ComponentType;
import org.osgp.shared.exceptionhandling.FunctionalException;
import org.osgp.shared.exceptionhandling.FunctionalExceptionType;

import com.alliander.osgp.dlms.DlmsDeviceMsg;
import com.alliander.osgp.shared.DeviceMsg;

public class DomainHelperService {

    private static final ComponentType COMPONENT_TYPE = ComponentType.PROTOCOL_DLMS;

    private DlmsDao dlmsDeviceDao;

//    private SessionProviderService sessionProviderService;
//    private JasperWirelessSmsClient jasperWirelessSmsClient;
//    private int jasperGetSessionRetries;
//    private int jasperGetSessionSleepBetweenRetries;

    /**
     * This method can be used to find an mBusDevice. For other devices, use
     * {@link #findDlmsDevice(DlmsDeviceMessageMetadata)} instead, as this will
     * also set the IP address.
     */
    public DlmsDevice findDlmsDevice(final DeviceMsg aDeviceMsg) throws FunctionalException {
        final DlmsDeviceMsg dlmsDeviceMsg = this.dlmsDeviceDao.findByDeviceId(aDeviceMsg.getDeviceId());
        if (dlmsDeviceMsg == null) {
            throw new FunctionalException(FunctionalExceptionType.UNKNOWN_DEVICE, COMPONENT_TYPE,
                    new ProtocolAdapterException("Unable to communicate with unknown device: " + aDeviceMsg.getDeviceId()));
        }
        
        DlmsDevice result = DlmsDevice.retrieve(aDeviceMsg);
        return result;
    }

    public DlmsDevice findDlmsDevice(final DeviceMsg aDeviceMsg, final String ipAddress)
            throws ProtocolAdapterException, FunctionalException {
        final DlmsDeviceMsg dlmsDeviceMsg = this.dlmsDeviceDao.findByDeviceId(aDeviceMsg.getDeviceId());
        if (dlmsDeviceMsg == null) {
            throw new ProtocolAdapterException("Unable to communicate with unknown device: " + aDeviceMsg.getDeviceId());
        }

        if (dlmsDeviceMsg.getSelcAccess()) {
//            dlmsDevice.setIpAddress(ipAddress);
        } else {
//            dlmsDevice.setIpAddress(this.getDeviceIpAddressFromSessionProvider(dlmsDevice));
        }

        DlmsDevice result = DlmsDevice.retrieve(aDeviceMsg);
        return result;
    }

//    private String getDeviceIpAddressFromSessionProvider(final DlmsDevice dlmsDevice) throws ProtocolAdapterException {
//
//        final SessionProvider sessionProvider = this.sessionProviderService.getSessionProvider(
//        		dlmsDevice.getDlmsDeviceMsg().getComProvider());
//        String deviceIpAddress = null;
//        try {
//            try {
//				deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getDlmsDeviceMsg().getIccId());
//			} catch (SessionProviderUnsupportedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//            if (deviceIpAddress != null) {
//                return deviceIpAddress;
//            }
//
//            // If the result is null then the meter is not in session (not
//            // awake).
//            // So wake up the meter and start polling for the session
//            this.jasperWirelessSmsClient.sendWakeUpSMS(dlmsDevice.getDlmsDeviceMsg().getIccId());
//            deviceIpAddress = this.pollForSession(sessionProvider, dlmsDevice);
//
//        } catch (final SessionProviderException e) {
//            throw new ProtocolAdapterException("", e);
//        }
//        if ((deviceIpAddress == null) || "".equals(deviceIpAddress)) {
//            throw new ProtocolAdapterException("Session provider: " + dlmsDevice.getDlmsDeviceMsg().getComProvider()
//                    + " did not return an IP address for device: " + dlmsDevice.getIdentification()
//                    + "and iccId: " + dlmsDevice.getDlmsDeviceMsg().getIccId());
//
//        }
//        return deviceIpAddress;
//    }

//    private String pollForSession(final SessionProvider sessionProvider, final DlmsDevice dlmsDevice)
//            throws ProtocolAdapterException {
//
//        String deviceIpAddress = null;
//        try {
//            for (int i = 0; i < this.jasperGetSessionRetries; i++) {
//                Thread.sleep(this.jasperGetSessionSleepBetweenRetries);
//                try {
//					deviceIpAddress = sessionProvider.getIpAddress(dlmsDevice.getDlmsDeviceMsg().getIccId());
//				} catch (SessionProviderUnsupportedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//                if (deviceIpAddress != null) {
//                    return deviceIpAddress;
//                }
//            }
//        } catch (final InterruptedException e) {
//            throw new ProtocolAdapterException(
//                    "Interrupted while sleeping before calling the sessionProvider.getIpAddress", e);
//        } catch (final SessionProviderException e) {
//            throw new ProtocolAdapterException("", e);
//        }
//        return deviceIpAddress;
//    }
}
