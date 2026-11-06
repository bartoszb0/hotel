package com.bd.hotelapp.guest.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.repository.GuestRepository;
import com.bd.hotelapp.guest.service.GuestService;
import com.bd.hotelapp.guest.util.EmailNormalizer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GuestServiceImpl implements GuestService {

    private final GuestRepository guestRepository;

    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    @Override
    @Transactional
    public Guest findOrCreate(String firstName, String lastName, String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);

        return guestRepository.findByEmail(normalizedEmail)
                .orElseGet(() -> guestRepository.save(new Guest(firstName, lastName, normalizedEmail)));
    }
}
