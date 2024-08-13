package com.jetcab.service.passenger.exception;

import com.jetcab.common.exception.NotFoundException;

public class PassengerNotFoundException extends NotFoundException {

    public static final String MESSAGE_KEY = "exception.not-found.passenger";

    public PassengerNotFoundException() {
        super(MESSAGE_KEY);
    }
}
