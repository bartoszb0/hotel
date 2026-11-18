package com.bd.hotelapp.user.security;

import com.bd.hotelapp.guest.util.EmailNormalizer;
import com.bd.hotelapp.user.domain.entity.User;
import com.bd.hotelapp.user.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String email = EmailNormalizer.normalize(username);
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("User %s does not exist".formatted(email))
        );
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles(user.getRole().name())
                .build();
    }
}
