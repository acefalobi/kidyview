package com.livetyping.utils.utils;

import android.util.Base64;

import java.util.List;

public final class StringUtils {
    public static final String DOT = ".";
    public static final String COMMA = ",";
    public static final String COLON = ":";
    public static final String EMPTY = "";
    public static final String SPACE = " ";
    public static final String SLASH = "/";
    public static final String DASH = "-";
    public static final String IMAGE = "image";
    public static final String APOSTROPHE_S = "'s";
    public static final String PNG_FORMAT = ".png";
    public static final String FOUR_SPACE = "    ";
    public static final String CARRET = "\n";
    public static final String BULLET = "\u2022";
    public static final String NON_BREAKING_SPACE = "\u00A0";
    public static final String ROUBLE = "\u20BD";
    public static final String NUMBER = "№";
    public static final String ZERO = "0";
    public static final String BETWEEN_TIME = " - ";
    public static final String LESS_THAN_XML = "&lt;";
    public static final String LESS_THAN = "<";
    public static final String AMPERSAND_XML = "&amp;";
    public static final String AMPERSAND = "&";
    public static final String STAR = "☆";
    public static final String THREEDOTS = "...";
    public static final String OPEN_BRACKET = "[";
    public static final String CLOSE_BRACKET = "]";
    public static final String IN = "in";

    private StringUtils() {
        // No instances.
    }

    public static boolean isEmailCorrect(String email) {
        boolean isEmailValidDefault = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        if (!isEmailValidDefault)
            return false;
        int lastIndexOfDot = email.lastIndexOf(DOT);
        String countryDomain = email.substring(lastIndexOfDot + 1);
        return countryDomain.length() >= 2;
    }

    public static boolean isBlank(CharSequence string) {
        return (string == null || string.toString().trim().length() == 0);
    }

    public static String valueOrDefault(String string, String defaultString) {
        return isBlank(string) ? defaultString : string;
    }

    public static String truncateAt(String string, int length) {
        return string.length() > length ? string.substring(0, length) : string;
    }

    public static boolean isCyrillic(String str) {
        return str.matches("[а-яА-Я' -]+");
    }

    public static boolean isCyrillicExtended(String str) {
        return str.matches("[а-яА-Я0-9',.?!;:)(\" -]+");
    }

    public static boolean isNubmers(String str) {
        return str.matches("[0-9]+");
    }

    public static boolean isPromoValid(String promo) {
        return promo.matches("[A-Z0-9]+") && promo.length() > 5;
    }

    public static boolean passwordsMatch(String pass1, String pass2) {
        return pass1.equals(pass2);
    }

    public static String encodeBase64(String str) {
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

    public static String capitalize(String str) {
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    public static String getSeparateString(List<?> objects, String separator) {
        if (objects == null || objects.isEmpty()) return StringUtils.EMPTY;
        StringBuilder stringBuilder = new StringBuilder();
        for (Object item : objects) {
            stringBuilder.append(item.toString());
            stringBuilder.append(separator);
        }
        stringBuilder.deleteCharAt(stringBuilder.lastIndexOf(separator));
        return stringBuilder.toString().trim();
    }

    public static String fillShortIds(String id, int length) {
        StringBuilder sb = new StringBuilder(id != null ? id : StringUtils.ZERO);
        while (sb.length() < length) {
            sb.insert(0, StringUtils.ZERO);
        }
        return sb.toString();
    }

    public static String fillShortIds(long id, int lenght) {
        return fillShortIds(String.valueOf(id), lenght);
    }

    public static String cutLongText(String title, int length) {
        if (title.length() > length) {
            title = title.substring(0, length) + StringUtils.THREEDOTS;
        }
        return title;
    }
}
