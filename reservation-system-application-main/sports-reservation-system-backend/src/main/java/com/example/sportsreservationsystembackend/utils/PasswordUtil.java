package com.example.sportsreservationsystembackend.utils;

import java.util.Random;

/**
 * This class represents password util
 *
 * @Author Radim Stejskal
 */
public class PasswordUtil {

    /**
     * This method generates random password
     * @param len length of password
     * @return random password
     */
    public static String generateRandomPassword(int len) {
        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghi"
                +"jklmnopqrstuvwxyz";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
