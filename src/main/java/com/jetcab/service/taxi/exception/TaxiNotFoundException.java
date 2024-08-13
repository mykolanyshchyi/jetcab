package com.jetcab.service.taxi.exception;

import com.jetcab.common.exception.NotFoundException;

public class TaxiNotFoundException extends NotFoundException {

    public static final String MESSAGE_KEY = "exception.not-found.taxi";

    public TaxiNotFoundException() {
        super(MESSAGE_KEY);
    }
}
