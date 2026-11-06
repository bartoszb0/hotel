package com.bd.hotelapp.guest.util;

import java.util.Locale;

public class EmailNormalizer {

    private EmailNormalizer() {
    }

    public static String normalize(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
