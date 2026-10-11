package com.tms.profilemanager.app.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PasswordGenerator {

    static String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:'\",<.>/?";
    static int DEFAULT_PASSWORD_LENGTH = 15;

    public static String generatePassword() {
        return RandomStringUtils.random(DEFAULT_PASSWORD_LENGTH, ALLOWED_CHARACTERS);
    }

    public static String generatePassword(int length) {
        return RandomStringUtils.random(length, ALLOWED_CHARACTERS);
    }

    public static String generatePassword(int length, char[] excludeSymbols) {
        String allowedCharacters = ALLOWED_CHARACTERS;
        for (char c : excludeSymbols) {
            allowedCharacters = allowedCharacters.replace(String.valueOf(c), "");
        }
        return RandomStringUtils.random(length, allowedCharacters);
    }
}
