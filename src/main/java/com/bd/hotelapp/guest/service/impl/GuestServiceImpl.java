package com.bd.hotelapp.guest.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.repository.GuestRepository;
import com.bd.hotelapp.guest.service.GuestService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    @Transactional
    public Guest findOrCreate(String firstName, String lastName, String email) {
        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);

        return guestRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> guestRepository.save(new Guest(firstName, lastName, normalizedEmail)));
    }
}
