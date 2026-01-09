package org.emiloanwithbill.util;

import java.security.MessageDigest;
import java.util.Base64;

public class PasswordUtil {
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hashed);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean verifyPassword(String raw, String storedHash) {
        return hashPassword(raw).equals(storedHash);
    }
}
