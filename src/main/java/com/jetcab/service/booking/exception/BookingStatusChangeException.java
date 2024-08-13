package com.jetcab.service.booking.exception;

import com.jetcab.common.exception.ForbiddenException;

public class BookingStatusChangeException extends ForbiddenException {
    public static final String MESSAGE_KEY = "exception.forbidden.booking-status-change";

    public BookingStatusChangeException() {
        super(MESSAGE_KEY);
    }
}
