/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.util.List;

public class ProfileGenericDataResponseDto extends ActionResponseDto {

    private static final long serialVersionUID = -156966569210717654L;

    private final ObisCodeValuesDto logicalName;
    private List<CaptureObjectDto> captureObjects;
    private List<ProfileEntryDto> profileEntries;

    public ProfileGenericDataResponseDto(ObisCodeValuesDto logicalName, List<CaptureObjectDto> captureObjects,
            List<ProfileEntryDto> profileEntries) {
        super();
        this.logicalName = logicalName;
        this.captureObjects = captureObjects;
        this.profileEntries = profileEntries;
    }

    public ObisCodeValuesDto getLogicalName() {
        return this.logicalName;
    }

    public List<CaptureObjectDto> getCaptureObjects() {
        return this.captureObjects;
    }

    public List<ProfileEntryDto> getProfileEntries() {
        return this.profileEntries;
    }
}
