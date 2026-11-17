package com.bd.hotelapp.user.exception;

import com.bd.hotelapp.common.exception.BusinessRuleException;

public class PasswordTooLongException extends BusinessRuleException {
    public PasswordTooLongException() {
        super("Password is too long");
    }
}
