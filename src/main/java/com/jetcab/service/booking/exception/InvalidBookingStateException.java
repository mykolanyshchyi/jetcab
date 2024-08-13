package com.jetcab.service.booking.exception;

import com.jetcab.common.exception.ConflictException;

public class InvalidBookingStateException extends ConflictException {

    public InvalidBookingStateException(String messageKey) {
        super(messageKey);
    }
}
