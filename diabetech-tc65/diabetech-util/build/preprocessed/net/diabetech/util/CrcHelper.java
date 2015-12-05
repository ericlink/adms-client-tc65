package net.diabetech.util;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class CrcHelper {

    private CrcHelper() {
    }
    private static final short[] crcTable = new short[256];

    static {
        for (int dividend = 0; dividend < 256; dividend++) {
            int remainder = dividend << 8;
            for (int bit = 8; bit > 0; --bit) {
                if ((remainder & 0x8000) != 0) {
                    remainder = (remainder << 1) ^ 0x1021; //x25 CCITT-CRC16
                } else {
                    remainder <<= 1;
                }
            }
            crcTable[dividend] = (short) remainder;
        }
    }

    /*
     * Calculate CRC per x25 CCITT-CRC16
     * @param buffer data to calculate the crc, calculated using entire buffer length
     */
    public static short calculateCrc16(byte[] buffer) {
        return calculateCrc16(buffer, 0, buffer.length);
    }

    /*
     * Calculate CRC per x25 CCITT-CRC16
     * @param buffer data to calculate the crc, calculated using offset and len length
     * @param offset starting byte to use in calculating crc
     * @len number of bytes to use in calculating crc
     */
    public static short calculateCrc16(byte[] buffer, int offset, int length) {
        short crc = (short) 0xffff; //x25 CCITT-CRC16 seed
        for (int i = 0; i < length; i++) {
            crc = (short) (crcTable[(buffer[i + offset] ^ (crc >>> 8)) & 0xff] ^ (crc << 8));
        }
        return crc;
    }
}
