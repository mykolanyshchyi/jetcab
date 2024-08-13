package com.jetcab.service.booking.exception;

import com.jetcab.common.exception.ForbiddenException;

public class CancelBookingException extends ForbiddenException {
    public static final String MESSAGE_KEY = "exception.forbidden.cancel-booking";

    public CancelBookingException() {
        super(MESSAGE_KEY);
    }
}
