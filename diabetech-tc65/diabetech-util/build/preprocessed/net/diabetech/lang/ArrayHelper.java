package net.diabetech.lang;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class ArrayHelper {

    private ArrayHelper() {
    }

    public static boolean equals(byte[] array1, byte[] array2) {
        if (array1 == null && array2 == null) {
            return true;
        }

        if (array1 == null || array2 == null) {
            return false;
        }

        if (array1.length != array2.length) {
            return false;
        }

        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false;
            }
        }

        return true;
    }

    /**
     * Copy bytes from src to dest, if they are available in src
     * @param src
     * @param start
     * @param end
     * @return
     */
    public static byte[] copyBytes(byte[] src, int start, int end) {
        int length = end - start;
        byte[] dest = new byte[length];

        for (int i = 0; i < length && i < src.length; i++) {
            dest[i] = src[i - start];
        }

        return dest;
    }
}
