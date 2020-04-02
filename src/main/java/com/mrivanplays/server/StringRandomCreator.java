package com.mrivanplays.server;

import java.util.concurrent.ThreadLocalRandom;

public class StringRandomCreator {

    private static final String data = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public static String generateRandomString(int length) {
        if (length < 5) {
            throw new IllegalArgumentException("Length should be at least 5 characters.");
        }

        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomCharAt = ThreadLocalRandom.current().nextInt(0, data.length());
            char randomChar = data.charAt(randomCharAt);

            builder.append(randomChar);
        }

        return builder.toString();
    }
}
