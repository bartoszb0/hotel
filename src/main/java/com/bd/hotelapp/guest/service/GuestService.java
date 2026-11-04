package com.bd.hotelapp.guest.service;

import com.bd.hotelapp.guest.domain.entity.Guest;

public interface GuestService {

    Guest findOrCreate(String firstName, String lastName, String email);
}
