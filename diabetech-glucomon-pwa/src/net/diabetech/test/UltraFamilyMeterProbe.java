/*
 */
package net.diabetech.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.diabetech.lang.ArrayHelper;
import net.diabetech.lang.StringHelper;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class UltraFamilyMeterProbe {

    public final static int METER_UNKOWN = 0;
    /**
     * Ultra and Ultra2 are treated the smae, they both use the Ultr2 protocol
     **/
    public final static int ULTRA_ULTRA2 = 1;
    public final static int ULTRA_MINI = 2;
    private InputStream is;
    private OutputStream os;
    final private static byte[] ULTRA_ULTRA2_COMMAND_INITIALIZE = new byte[]{
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) 'S', (byte) 0x0D, (byte) 0x0D,
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) 'S', (byte) 0x0D, (byte) 0x0D,
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) '@'
    //A5 from ultra no need for this?
    //(byte)0xff, (byte)0xff, (byte)0xff, (byte)0x11, (byte)0x0D, (byte)'D', (byte)'M', (byte)'@'
    };
    final private static byte[] ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT = new byte[]{
        (byte) 0x02, (byte) 0x06, (byte) 0x08, (byte) 0x03, (byte) 0xC2, (byte) 0x62
    };
    final private static byte[] COMMAND_INITIALIZE_ACKNOWLEDGEMENT = new byte[]{
        (byte) 0x02, (byte) 0x06, (byte) 0x0c, (byte) 0x03, (byte) 0x06, (byte) 0xae
    };

    public UltraFamilyMeterProbe(final InputStream is, final OutputStream os) {
        this.is = is;
        this.os = os;
    }

    /**
     **/
    public int probe() throws IOException {
        Logger.log("probe()");
        writeUltraUltra2(ULTRA_ULTRA2_COMMAND_INITIALIZE);
        byte[] response = readAllMeterData();
        Logger.log("ULTRA_ULTRA2_COMMAND_INITIALIZE", StringHelper.format(new String(response), "0x"), new String(response));
        if (response != null && response.length > 0 && new String(response).indexOf("S 0053") > -1) {
            return ULTRA_ULTRA2;
        } else if (response != null && response.length > 0) {
            // noise on the line, often this is 0x00's with an ultra2
            // so don't send mini commands to avoid ATE message on ultra2
            return METER_UNKOWN;
        }
        writeUltraMini(ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT);
        response = readAllMeterData();
        Logger.log("ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT", StringHelper.format(new String(response), "0x"));
        if (ArrayHelper.equals(response, COMMAND_INITIALIZE_ACKNOWLEDGEMENT)) {
            return ULTRA_MINI;
        }
        ;

        return METER_UNKOWN;
    }

    private void writeUltraUltra2(final byte[] b) {
        try {
            for (int i = 0; i < b.length; i++) {
                os.write(b[i]);
                ThreadHelper.sleep(10);
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    private void writeUltraMini(final byte[] b) {
        try {
            for (int i = 0; i < b.length; i++) {
                os.write(b[i]);
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    /**
     * Wait a few millis for meter to turn and start data.
     * These are short messages so they come through quickly;
     * reading until no data is available works and gets all data off of the serial line
     * @return data read from the meter
     */
    private byte[] readAllMeterData() throws IOException {
        ThreadHelper.sleep(100); // req'd or response doesn't show up for read all
        ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
        while (is.available() > 0) {
            baos.write(is.read());
        }
        return baos.toByteArray();
    }
}
