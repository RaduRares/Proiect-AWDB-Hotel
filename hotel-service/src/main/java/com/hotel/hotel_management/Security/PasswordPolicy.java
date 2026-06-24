package com.hotel.hotel_management.Security;

import java.util.Set;

public class PasswordPolicy {

    private static final int MIN_LENGTH = 8;

    private static final Set<String> BLACKLIST = Set.of(
        "password", "password1", "password123",
        "12345678", "123456789", "1234567890",
        "qwerty123", "qwertyui",
        "iloveyou", "letmein1", "welcome1",
        "monkey123", "dragon123", "master123",
        "sunshine", "princess", "football",
        "abc12345", "pass1234", "test1234",
        "admin123", "admin1234", "administrator"
    );

    public static String validate(String password) {
        if (password == null || password.isBlank()) {
            return "Parola nu poate fi goala.";
        }
        if (password.length() < MIN_LENGTH) {
            return "Parola trebuie sa aiba cel putin " + MIN_LENGTH + " caractere.";
        }
        if (BLACKLIST.contains(password.toLowerCase())) {
            return "Parola este prea comuna. Alege o parola mai unica.";
        }
        return null;
    }
}
