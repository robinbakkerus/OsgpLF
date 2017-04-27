/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

public enum NotificationTypeDto {
    ADD_METER,
    FIND_EVENTS,
    REQUEST_PERIODIC_METER_DATA,
    SYNCHRONIZE_TIME,
    REQUEST_SPECIAL_DAYS,
    SET_ALARM_NOTIFICATIONS,
    SET_CONFIGURATION_OBJECT,
    SET_ADMINISTRATIVE_STATUS,
    GET_ADMINISTRATIVE_STATUS,
    SET_ACTIVITY_CALENDAR,
    REQUEST_ACTUAL_METER_DATA,
    READ_ALARM_REGISTER,
    PUSH_NOTIFICATION_ALARM,
    SEND_WAKEUP_SMS,
    GET_SMS_DETAILS,
    REPLACE_KEYS,
    SET_PUSH_SETUP_ALARM,
    SET_PUSH_SETUP_SMS,
    GET_CONFIGURATION_OBJECTS,
    SET_ENCRYPTION_KEY_EXCHANGE_ON_G_METER,
    HANDLE_BUNDLED_ACTIONS,
    GET_ASSOCIATION_LN_OBJECTS;
}