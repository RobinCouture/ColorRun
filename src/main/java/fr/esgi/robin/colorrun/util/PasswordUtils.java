package fr.esgi.robin.colorrun.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtils {
    private static final int ROUNDS = 10;

    public static String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(ROUNDS));
    }

    public static boolean checkPassword(String password, String hashedPassword) {
        return BCrypt.checkpw(password, hashedPassword);
    }
}
