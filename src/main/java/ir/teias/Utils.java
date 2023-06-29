package ir.teias;

import org.apache.commons.lang3.RandomStringUtils;

public class Utils {
    public static String generateRandomString(int bits) {
        return RandomStringUtils.randomAlphabetic(bits).toLowerCase();
    }
}
