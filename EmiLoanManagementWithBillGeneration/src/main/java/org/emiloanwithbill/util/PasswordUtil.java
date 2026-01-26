package org.emiloanwithbill.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {


    public static String hashPassword(String password) {
        try {
            return BCrypt.hashpw(password, BCrypt.gensalt());
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String raw, String storedHash) {
        try {
            return BCrypt.checkpw(raw, storedHash);
        } catch (Exception e) {
            return false;
        }
    }
}
