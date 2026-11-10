package com.bd.hotelapp.availability.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

public class InvalidSearchDatesException extends BusinessRuleException {
    public InvalidSearchDatesException(String message) {
        super(message);
    }
}
