package com.bd.hotelapp.common.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthenticatedAuthorizationManager;
import org.springframework.security.authorization.AuthorityAuthorizationManager;
import org.springframework.security.authorization.AuthorizationManagers;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
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
                        .requestMatchers(V1 + "/staff/**").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, V1 + "/auth/register", V1 + "/auth/login").permitAll()

                        // public read, staff writes
                        .requestMatchers(HttpMethod.GET, V1 + "/room-types/**", V1 + "/rooms/**").permitAll()
                        .requestMatchers(V1 + "/room-types/**", V1 + "/rooms/**").hasRole("STAFF")

                        // reservations
                        .requestMatchers(HttpMethod.POST, V1 + "/reservations").access(
                                AuthorizationManagers.anyOf(
                                        AuthenticatedAuthorizationManager.anonymous(),
                                        AuthorityAuthorizationManager.hasRole("STAFF")
                                )
                        )
                        .requestMatchers(V1 + "/reservations/me").hasRole("GUEST")
                        .requestMatchers(HttpMethod.GET, V1 + "/reservations").hasRole("STAFF")
                        .requestMatchers(HttpMethod.POST, V1 + "/reservations/*/cancel").hasAnyRole("GUEST", "STAFF")
                        .requestMatchers(HttpMethod.GET, V1 + "/reservations/*").permitAll()

                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    // Reads the role from the "role" claim as-is (it already contains the ROLE_ prefix)
    private JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authorities = new JwtGrantedAuthoritiesConverter();
        authorities.setAuthoritiesClaimName("role");
        authorities.setAuthorityPrefix("");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authorities);
        return converter;
    }
}
