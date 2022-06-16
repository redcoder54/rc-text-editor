package redcoder.rctexteditor.utils;

import java.util.Iterator;
import java.util.StringJoiner;

public class StringUtils {

    public static String join(Iterable<String> iterable,String delimiter) {
        StringJoiner joiner = new StringJoiner(delimiter);
        Iterator<String> it = iterable.iterator();
        while (it.hasNext()) {
            joiner.add(it.next());
        }
        return joiner.toString();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isContainNonEnglishLetter(String str) {
        for (char c : str.toCharArray()) {
            if (!((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
                return true;
            }
        }
        return false;
    }
}
