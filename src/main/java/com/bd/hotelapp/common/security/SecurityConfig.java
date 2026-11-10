package com.bd.hotelapp.common.security;

import com.bd.hotelapp.common.web.ApiPaths;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // dokumentacja i strona błędu: zawsze dostępne
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**", "/error").permitAll()

                        // publiczne: przeglądanie oferty, wyszukiwanie, rezerwacja bez konta, rejestracja
                        .requestMatchers(HttpMethod.GET, ApiPaths.V1 + "/room-types/**", ApiPaths.V1 + "/rooms/available").permitAll()
                        .requestMatchers(HttpMethod.POST, ApiPaths.V1 + "/reservations", ApiPaths.V1 + "/auth/register").permitAll()

                        // zarządzanie typami i pokojami: tylko personel
                        .requestMatchers(ApiPaths.V1 + "/room-types/**", ApiPaths.V1 + "/rooms/**").hasRole("STAFF")

                        // wszystko inne: dowolny zalogowany
                        .anyRequest().authenticated()
                )
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}