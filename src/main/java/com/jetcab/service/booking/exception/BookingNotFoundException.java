package com.jetcab.service.booking.exception;

import com.jetcab.common.exception.NotFoundException;

public class BookingNotFoundException extends NotFoundException {

    public static final String MESSAGE_KEY = "exception.not-found.booking";

    public BookingNotFoundException() {
        super(MESSAGE_KEY);
    }
}
