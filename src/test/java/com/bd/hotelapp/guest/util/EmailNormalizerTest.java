package com.bd.hotelapp.guest.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class EmailNormalizerTest {

    @Test
    void shouldLowercaseAndTrimEmail() {
        String email = "  Jan.KoWalski@exAmple.COM ";

        String result = EmailNormalizer.normalize(email);

        assertThat(result).isEqualTo("jan.kowalski@example.com");
    }

    @Test
    void shouldLeaveNormalizedEmailUnchanged() {
        String email = "anna.kowalska@example.com";

        String result = EmailNormalizer.normalize(email);

        assertThat(result).isEqualTo(email);
    }
}
