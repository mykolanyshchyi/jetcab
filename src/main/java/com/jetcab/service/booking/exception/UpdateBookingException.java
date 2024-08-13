package com.jetcab.service.booking.exception;

import com.jetcab.common.exception.ForbiddenException;

public class UpdateBookingException extends ForbiddenException {
    public static final String MESSAGE_KEY = "exception.forbidden.update-booking";

    public UpdateBookingException() {
        super(MESSAGE_KEY);
    }
}
