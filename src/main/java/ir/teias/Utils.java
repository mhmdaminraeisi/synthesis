package ir.teias;

import ir.teias.model.Table;
import org.apache.commons.lang3.RandomStringUtils;

public class Utils {
    public static String generateRandomString(int bits) {
        return RandomStringUtils.randomAlphabetic(bits).toLowerCase();
    }
}
