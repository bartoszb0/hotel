package com.bd.hotelapp.reservation.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

public class InvalidStayDateException extends BusinessRuleException {
    public InvalidStayDateException(String message) {
        super(message);
    }
}
