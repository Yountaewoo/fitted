package com.fitted.user;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String supabaseId;

    private String password;

    @Column(unique = true)
    private String email;

    private String name;

    protected User() {
    }

    public User(String supabaseId, String password, String email, String name) {
        this.supabaseId = supabaseId;
        this.password = password;
        this.email = email;
        this.name = name;
    }
}
