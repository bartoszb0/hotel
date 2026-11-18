package com.bd.hotelapp.user.service.impl;

import com.bd.hotelapp.guest.domain.entity.Guest;
import com.bd.hotelapp.guest.util.EmailNormalizer;
import com.bd.hotelapp.user.domain.entity.User;
import com.bd.hotelapp.user.domain.entity.UserRole;
import com.bd.hotelapp.user.exception.PasswordTooLongException;
import com.bd.hotelapp.user.exception.UserNotFoundException;
import com.bd.hotelapp.user.exception.UserWithEmailAlreadyExistsException;
import com.bd.hotelapp.user.repository.UserRepository;
import com.bd.hotelapp.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private static final int MAX_PASSWORD_BYTES = 72;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public User createGuestAccount(String email, String rawPassword, Guest guest) {
        return createAccount(email, rawPassword, UserRole.GUEST, guest);
    }

    @Override
    @Transactional
    public User createStaffAccount(String email, String rawPassword) {
        return createAccount(email, rawPassword, UserRole.STAFF, null);
    }

    @Override
    public User getUserByEmail(String email) {
        String normalizedEmail = EmailNormalizer.normalize(email);
        return userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new UserNotFoundException(normalizedEmail));
    }

    @Override
    public UUID getGuestId(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));

        if (user.getGuest() == null) throw new IllegalStateException("User has no guest profile.");

        return user.getGuest().getId();
    }


    private User createAccount(String email, String rawPassword, UserRole role, Guest guest) {
        String normalizedEmail = EmailNormalizer.normalize(email);
        validatePasswordLength(rawPassword);
        validateEmailNotTaken(normalizedEmail);

        User user = new User(normalizedEmail, passwordEncoder.encode(rawPassword), role, guest);
        return userRepository.save(user);
    }

    // BCrypt limit is 72 bytes, not chars - Polish
    private void validatePasswordLength(String rawPassword) {
        if (rawPassword.getBytes(StandardCharsets.UTF_8).length > MAX_PASSWORD_BYTES)
            throw new PasswordTooLongException();
    }

    private void validateEmailNotTaken(String email) {
        if (userRepository.existsByEmail(email)) throw new UserWithEmailAlreadyExistsException(email);
    }
}
