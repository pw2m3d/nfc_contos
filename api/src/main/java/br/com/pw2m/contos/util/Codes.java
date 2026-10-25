package br.com.pw2m.contos.util;

import java.security.SecureRandom;
import java.util.Base64;

public final class Codes {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final char[] ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789".toCharArray();

    private Codes() {
    }

    public static String publicCode(String storyCode) {
        return "PW2M-" + storyCode + "-" + random(6);
    }

    public static String pin() {
        return "%06d".formatted(RANDOM.nextInt(1_000_000));
    }

    public static String ticket() {
        byte[] bytes = new byte[32];
        RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private static String random(int length) {
        char[] value = new char[length];
        for (int index = 0; index < length; index++) {
            value[index] = ALPHABET[RANDOM.nextInt(ALPHABET.length)];
        }
        return new String(value);
    }
}
