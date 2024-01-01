package com.junit.entity;

import java.util.Arrays;
import java.util.Optional;

public enum Role {
    USER,
    ADMIN;

    public static Optional<Role> find(String role) {
        return Arrays.stream(values())
                .filter(r -> r.name().equals(role))
                .findFirst();
    }
}
