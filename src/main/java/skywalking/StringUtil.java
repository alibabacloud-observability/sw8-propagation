package skywalking;

/**
 * @author carpela <luhao.wh@alibaba-inc.com>
 * @date 2022/12/1
 */

import java.util.function.Consumer;

public final class StringUtil {
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        return str == null || isEmpty(str.trim());
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static void setIfPresent(String value, Consumer<String> setter) {
        if (isNotEmpty(value)) {
            setter.accept(value);
        }
    }

    public static String join(final char delimiter, final String... strings) {
        if (strings.length == 0) {
            return null;
        }
        if (strings.length == 1) {
            return strings[0];
        }
        int length = strings.length - 1;
        for (final String s : strings) {
            if (s == null) {
                continue;
            }
            length += s.length();
        }
        final StringBuilder sb = new StringBuilder(length);
        if (strings[0] != null) {
            sb.append(strings[0]);
        }
        for (int i = 1; i < strings.length; ++i) {
            if (!isEmpty(strings[i])) {
                sb.append(delimiter).append(strings[i]);
            } else {
                sb.append(delimiter);
            }
        }
        return sb.toString();
    }

    public static boolean substringMatch(CharSequence str, int index, CharSequence substring) {
        if (index + substring.length() > str.length()) {
            return false;
        }
        for (int i = 0; i < substring.length(); i++) {
            if (str.charAt(index + i) != substring.charAt(i)) {
                return false;
            }
        }
        return true;
    }

    public static String cut(String str, int threshold) {
        if (isEmpty(str) || str.length() <= threshold) {
            return str;
        }
        return str.substring(0, threshold);
    }

    public static String trim(final String str, final char ch) {
        if (isEmpty(str)) {
            return null;
        }

        final char[] chars = str.toCharArray();

        int i = 0, j = chars.length - 1;
        // noinspection StatementWithEmptyBody
        for (; i < chars.length && chars[i] == ch; i++) {
        }
        // noinspection StatementWithEmptyBody
        for (; j > 0 && chars[j] == ch; j--) {
        }

        return new String(chars, i, j - i + 1);
    }
}
