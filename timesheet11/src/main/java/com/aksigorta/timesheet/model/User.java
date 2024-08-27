package com.aksigorta.timesheet.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(name="email",nullable = false, unique = true)
    private String email;

    @Column(name="password",nullable = false)
    private String password;


    @Enumerated(EnumType.STRING)
    @Column(name="role",nullable = false)
    private Role role;

    @Column(nullable = false)
    @NotNull
    private LocalDate registrationDate = LocalDate.now();

}

