package com.bd.hotelapp.user.domain.entity;

import com.bd.hotelapp.guest.domain.entity.Guest;
import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    protected User() {
    }

    public User(String email, String passwordHash, UserRole role, Guest guest) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.guest = guest;
    }

    public UUID getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public UserRole getRole() {
        return role;
    }

    public Guest getGuest() {
        return guest;
    }
}
