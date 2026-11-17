package com.bd.hotelapp.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static com.bd.hotelapp.common.web.ApiPaths.V1;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                        // documentation and error
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()

                        // accounts
                        .requestMatchers(HttpMethod.POST, V1 + "/auth/register").permitAll()
                        .requestMatchers(V1 + "/staff/**").hasRole("STAFF")

                        // public read, staff writes
                        .requestMatchers(HttpMethod.GET, V1 + "/room-types/**", V1 + "/rooms/**").permitAll()
                        .requestMatchers(V1 + "/room-types/**", V1 + "/rooms/**").hasRole("STAFF")

                        // reservations
                        .requestMatchers(HttpMethod.POST, V1 + "/reservations").permitAll()
                        .requestMatchers(HttpMethod.GET, V1 + "/reservations/me").hasRole("GUEST")
                        .requestMatchers(HttpMethod.GET, V1 + "/reservations").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, V1 + "/reservations/*/cancel").hasAnyRole("GUEST", "STAFF")
                        .requestMatchers(HttpMethod.GET, V1 + "/reservations/*").permitAll()

                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
