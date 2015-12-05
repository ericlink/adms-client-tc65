package net.diabetech.onetouchfamily;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class OneTouchFamilyProbe {

    OneTouchFamilyProbe(final InputStream is, final OutputStream os) {
        this.is = is;
        this.os = os;
    }

    int probe() throws IOException {
        Logger.log("probe()");
        writeUltraUltra2(ULTRA_ULTRA2_COMMAND_INITIALIZE);
        byte[] response = readAllMeterData();
        Logger.logByteArray("ULTRA_ULTRA2_COMMAND_INITIALIZE response", response);
        String responseString = new String(response);
        if (response != null && response.length > 0 && responseString.indexOf(ULTRA_ULTRA2_INITIALIZE_RESPONSE_TOKEN) > -1) {
            int ultra2SerialNumberEndsWithY = responseString.indexOf(ULTRA2_SERIAL_NUMBER_TOKEN);
            Logger.log("ultra2SerialNumberEndsWithY", ultra2SerialNumberEndsWithY);
            if (ultra2SerialNumberEndsWithY > ULTRA2_SERIAL_NUMBER_TOKEN_MIN_INDEX) {
                return ULTRA2;
            } else {
                return ULTRA2;//TODO TEST  S 0053^M all that is returned? - UK METERS ONLY?
//TEST     return ULTRA;
            }
        } else if (response != null && response.length > 0) {
            // noise on the line, often this is 0x00's with an ultra2
            // so return now,
            // we don't send mini commands in this case
            // to avoid the ATE message on ultra2
            return METER_UNKOWN;
        }

        /** v3
        writeUltraMini( ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT );
        response = readAllMeterData();
        Logger.logByteArray("ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT response", response);
        if ( ArrayHelper.equals( response, COMMAND_INITIALIZE_ACKNOWLEDGEMENT ) ) {
        return ULTRA_MINI;
        };

         */
        return METER_UNKOWN;
    }

    private void writeUltraUltra2(final byte[] b) {
        try {
            for (int i = 0; i < b.length; i++) {
                os.write(b[i]);
                //NB: For Ultra2, if this writes too quickly (10ms sleep),
                //    ATE errors / ERROR 1 occurs (Instead of PC)
                ThreadHelper.sleep(20);
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

    //from link layer approach; read all data w/ retries
    private byte[] readAllMeterData() {
        try {
            StopWatch sw = new StopWatch("readAllMeterData()");
            ThreadHelper.sleep(INITIAL_SLEEP_WAIT_FOR_METER_TO_BECOME_READY);
            int numberOfTimesContinued = 0;
            int available = 0;
            int maxAvailable = 0;
            int timesThroughLoop = 0;
            baos.reset();
            Logger.log("maxTriesWaitAtEnd (+ interim waits)", MAX_RETRIES * RETRY_SLEEP);
            while (numberOfTimesContinued < MAX_RETRIES) {
                ThreadHelper.sleep(RETRY_SLEEP);
                while ((available = is.available()) > 0) {
                    //verbose Logger.log("available", available);
                    maxAvailable = available > maxAvailable ? available : maxAvailable;
                    int bytesToRead = available >= readBuffer.length ? readBuffer.length - 1 : available;
                    is.read(readBuffer, 0, bytesToRead);
                    baos.write(readBuffer, 0, bytesToRead);
                    numberOfTimesContinued = 0;
                    timesThroughLoop++;
                }
                numberOfTimesContinued++;
            }
            Logger.log("timesThroughLoop", timesThroughLoop);
            Logger.log("maxAvailable", maxAvailable);
            Logger.log("numberOfTimesContinued", numberOfTimesContinued);
            Logger.log("readMeterData()", String.valueOf(baos.size()), sw.getElapsedTimeMessage());
        } catch (IOException ex) {
            Logger.log(ex);
            baos.reset();
        } finally {
            return baos.toByteArray();
        }
    }

    //from link layer approach; read all data w/ retries
    private static final int INITIAL_SLEEP_WAIT_FOR_METER_TO_BECOME_READY = 10;
    private static final int MAX_RETRIES = 15;
    private static final int RETRY_SLEEP = 100;
    private final byte[] readBuffer = new byte[1024];

    final static int METER_UNKOWN = 0;
    final static int ULTRA = 1;
    final static int ULTRA2 = 2;
    final static int ULTRA_MINI = 3;
    private InputStream is;
    private OutputStream os;
    private static ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
    final private static byte[] ULTRA_ULTRA2_COMMAND_INITIALIZE = new byte[]{
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) 'S', (byte) 0x0D, (byte) 0x0D,
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) 'S', (byte) 0x0D, (byte) 0x0D,
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) '@'
    };
    private final static String ULTRA_ULTRA2_INITIALIZE_RESPONSE_TOKEN = "S 0053";
    private final static String ULTRA2_SERIAL_NUMBER_TOKEN = "Y\"";
    private final int ULTRA2_SERIAL_NUMBER_TOKEN_MIN_INDEX = 10;
    final private static byte[] ULTRA_MINI_COMMAND_INITIALIZE_DISCONNECT = new byte[]{
        (byte) 0x02, (byte) 0x06, (byte) 0x08, (byte) 0x03, (byte) 0xC2, (byte) 0x62
    };
    final private static byte[] COMMAND_INITIALIZE_ACKNOWLEDGEMENT = new byte[]{
        (byte) 0x02, (byte) 0x06, (byte) 0x0c, (byte) 0x03, (byte) 0x06, (byte) 0xae
    };
}
