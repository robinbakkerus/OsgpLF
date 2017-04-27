/**
 * Copyright 2016 Smart Society Services B.V.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 */
package org.osgp.pa.dlms.exceptions;

public abstract class RetryableException extends RuntimeException {

    private static final long serialVersionUID = 5671612098681860147L;

    public RetryableException(final String message, final Throwable cause, final boolean enableSuppression,
            final boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RetryableException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RetryableException(final String message) {
        super(message);
    }

    public RetryableException(final Throwable cause) {
        super(cause);
    }
}
