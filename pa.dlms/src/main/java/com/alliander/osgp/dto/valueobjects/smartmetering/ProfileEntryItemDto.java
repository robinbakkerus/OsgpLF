/**
 * Copyright 2015 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package com.alliander.osgp.dto.valueobjects.smartmetering;

import java.io.Serializable;
import java.util.List;

public class ProfileEntryItemDto implements Serializable {

    private static final long serialVersionUID = 2123390296585369209L;

    private List<ProfileEntryDto> profileEntry;

    public ProfileEntryItemDto(List<ProfileEntryDto> profileEntry) {
        super();
        this.profileEntry = profileEntry;
    }

    public List<ProfileEntryDto> getProfileEntry() {
        return this.profileEntry;
    }
}
