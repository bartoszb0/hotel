package com.bd.hotelapp.user.bootstrap;

import com.bd.hotelapp.user.domain.entity.UserRole;
import com.bd.hotelapp.user.repository.UserRepository;
import com.bd.hotelapp.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;



@Component
public class StaffAccountInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StaffAccountInitializer.class);

    private final UserRepository userRepository;
    private final UserService userService;
    private final String email;
    private final String password;

    public StaffAccountInitializer(
            UserRepository userRepository,
            UserService userService,
            @Value("${app.bootstrap.staff.email}") String email,
            @Value("${app.bootstrap.staff.password}") String password
    ) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.email = email;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByRole(UserRole.STAFF)) return;

        if (email.isBlank() || password.isBlank()) throw new IllegalStateException(
                "No STAFF account exists. Set BOOTSTRAP_STAFF_EMAIL and BOOTSTRAP_STAFF_PASSWORD to create the initial one."
        );

        userService.createStaffAccount(email, password);
        log.info("Created initial STAFF account: {}", email);
    }
}