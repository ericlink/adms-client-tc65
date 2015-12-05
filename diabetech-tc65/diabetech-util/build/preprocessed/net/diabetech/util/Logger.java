package net.diabetech.util;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class Logger {

    private static long lastLogTimeMillis = System.currentTimeMillis();

    /**
     * I only want one logger in the J2ME VM
     **/
    private Logger() {
    }

    private static void beginLog() {
        if (isDebug) {
            // Use log via System.out built into tc65
            currentTimeMillis = System.currentTimeMillis();
            long elapsedTimeMillis = currentTimeMillis - lastLogTimeMillis;
            System.out.print(NEWLINE);
            System.out.print(currentTimeMillis);
            System.out.print(SPACES_2);
            if (elapsedTimeMillis < 10) {
                System.out.print(SPACES_4);
            } else if (elapsedTimeMillis < 100) {
                System.out.print(SPACES_3);
            } else if (elapsedTimeMillis < 1000) {
                System.out.print(SPACES_2);
            } else if (elapsedTimeMillis < 10000) {
                System.out.print(SPACES_1);
            }
            System.out.print(elapsedTimeMillis);
            System.out.print(SPACES_2);
            System.out.print(BRACKET_START);
        }
    }

    private static void endLog() {
        if (isDebug) {
            System.out.print(BRACKET_END);
            lastLogTimeMillis = currentTimeMillis;
        }
    }

    public static void logByteArray(String messageOne, byte[] array) {
        logByteArray(messageOne, array, false);
    }

    public static void logByteArray(String messageOne, byte[] array, boolean printHex) {
        if (isDebug) {
            Logger.log(messageOne, String.valueOf(array.length));
            for (int i = 0; i < array.length; i++) {
                if (printHex) {
                    if (i > 0 && i % 8 == 0) {
                        System.out.print('\n');
                    }
                    String hex = Integer.toHexString(array[i]);
                    if (hex.length() == 1) {
                        System.out.print('0');
                    }
                    System.out.print(hex);
                    System.out.print(' ');
                } else {
                    System.out.print((char) array[i]);
                }
            }
            /// neat, when printing a string buffer,
            /// only the first 1000 characters are printed.... ooofa
            //            StringBuffer sb = new StringBuffer(array.length);
            //            for (int i = 0; i < array.length; i++) {
            //                sb.append((char) array[i]);
            //            }
            //            log(sb.toString());
        }
    }

    public static void log(Object messageOne) {
        if (isDebug) {
            log(messageOne, null, null, null);
        }
    }

    public static void log(Object messageOne, Object messageTwo) {
        if (isDebug) {
            log(messageOne, messageTwo, null, null);
        }
    }

    public static void log(Object messageOne, Object messageTwo, Object messageThree) {
        if (isDebug) {
            log(messageOne, messageTwo, messageThree, null);
        }
    }

    public static void log(Object messageOne, Object messageTwo, Object messageThree, Object messageFour) {
        if (isDebug) {
            beginLog();
            if (messageOne != null && messageTwo != null && messageThree != null && messageFour != null) {
                System.out.print(messageOne);
                System.out.print(BRACKET_END_START);
                System.out.print(messageTwo);
                System.out.print(BRACKET_END_START);
                System.out.print(messageThree);
                System.out.print(BRACKET_END_START);
                System.out.print(messageFour);
            } else if (messageOne != null && messageTwo != null && messageThree != null) {
                System.out.print(messageOne);
                System.out.print(BRACKET_END_START);
                System.out.print(messageTwo);
                System.out.print(BRACKET_END_START);
                System.out.print(messageThree);
            } else if (messageOne != null && messageTwo != null) {
                System.out.print(messageOne);
                System.out.print(BRACKET_END_START);
                System.out.print(messageTwo);
            } else if (messageOne != null) {
                System.out.print(messageOne);
            }
            endLog();
        }
    }

    public static void log(String string, long value) {
        if (isDebug) {
            log(string, String.valueOf(value));
        }
    }

    public static void log(String string, boolean cond) {
        if (isDebug) {
            log(string, String.valueOf(cond));
        }
    }

    public static boolean isDebug() {
        return isDebug;
    }

    public static void setDebug(boolean b) {
        isDebug = b;
    }
    private static long currentTimeMillis = 0;
    private static boolean isDebug = false;
    final private static char BRACKET_START = '[';
    final private static char BRACKET_END = ']';
    final private static char[] BRACKET_END_START = new char[]{BRACKET_END, BRACKET_START};
    final private static char[] SPACES_1 = new char[]{' '};
    final private static char[] SPACES_2 = new char[]{' ', ' '};
    final private static char[] SPACES_3 = new char[]{' ', ' ', ' '};
    final private static char[] SPACES_4 = new char[]{' ', ' ', ' ', ' '};
    final private static char NEWLINE = '\n';
}
