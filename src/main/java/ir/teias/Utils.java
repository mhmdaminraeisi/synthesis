package ir.teias;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String generateRandomString(int bits) {
        return RandomStringUtils.randomAlphabetic(bits).toLowerCase();
    }

    public static String replaceQueryNames(String query) {
        int id = 1;
        List<String> queryNames = new ArrayList<String>();
        Matcher matcher = Pattern.compile("[)] [a-z]{6}")
                .matcher(query);
        while (matcher.find()) {
            queryNames.add(matcher.group());
        }
        for (String match : queryNames) {
            while (query.contains("t" + id)) {
                id += 1;
            }
            String tableName = "t" + id;
            query = query.replace(match.substring(2), tableName);
        }
        return query;
    }
}
