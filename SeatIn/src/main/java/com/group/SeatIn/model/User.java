package com.group.SeatIn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id", nullable = false, updatable = false)
    private Long userId; //User ID

    @Column(name = "username", length = 50, nullable = false)
    private String username; // VARCHAR(50) first name

    @Column(name = "email", length = 50, nullable = false, unique = true)
    private String email; // VARCHAR(50) unique email

    @Column(name = "password", length = 255, nullable = false)
    private String password; // VARBINARY(32) hashed password

    @Column(name = "is_admin", nullable = false)
    private boolean isAdmin; // BOOLEAN is admin

    // --- Constructors ---
    public User() {}

    public User(String username, String email, String password, boolean isAdmin) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.isAdmin = isAdmin;
        ;
    }
}