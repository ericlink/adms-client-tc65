package net.diabetech.lang;

import java.util.Vector;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class StringHelper {

    private StringHelper() {
    }

    public static String getDigits(String stringInput) {

        char[] chars = stringInput.toCharArray();
        StringBuffer cleanedInput = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            if (Character.isDigit(chars[i])) {
                cleanedInput.append(chars[i]);
            }
        }
        return cleanedInput.toString();
    }

    public static String getLastN(String s, int numToGet) {
        return s.substring(s.length() - numToGet, s.length());
    }

    /**
     * Tokenizer for strings since not available in device profile packages
     * @return Vector of <i>trimmed</i> elements from string
     **/
    public static Vector split(String input, String token) {
        int startIndex = 0;
        int tokenIndex = 0;
        Vector elements = new Vector();
        do {
            int currentTokenIndex = input.indexOf(token, startIndex);
            tokenIndex = currentTokenIndex == -1 ? input.length() : currentTokenIndex;
            String element = input.substring(startIndex, tokenIndex);
            elements.addElement(element.trim());
            startIndex = tokenIndex + 1;
        } while (tokenIndex < input.length());

        return elements;
    }

    /**
     * Provide a format helper since format is not in device packages
     * @param format ## - two digits, 0x - format as hex dump
     **/
    public static String format(String value, String format) {
        if (format.equals("##") && value.length() == 1) {
            return "0" + value;
        }

        if (format.equals("0x")) {
            return toHexString(value);
        }

        return value;
    }

    private static String toHexString(String input) {
        StringBuffer sb = new StringBuffer();
        byte[] bytes = input.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i]);
            if (hex.length() == 1) {
                sb.append("0");
            } else if (hex.length() > 2) {
                hex = hex.substring(hex.length() - 2, hex.length());
            }

            sb.append(hex);
            sb.append(" ");
        }
        return sb.toString();
    }

    public static String substringBetween(
            final String str,
            final String startToken,
            final String endToken) {
        //-1 if not found
        int firstToken = str.indexOf(startToken) + 1;
        int secondToken = str.indexOf(endToken, firstToken);
        // make safe if not found
        firstToken = firstToken < 0 ? 0 : firstToken;
        secondToken = secondToken < 0 ? 0 : secondToken;
        return str.substring(firstToken, secondToken);
    }
}
