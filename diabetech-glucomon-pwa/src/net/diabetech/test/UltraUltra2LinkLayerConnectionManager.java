package net.diabetech.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.diabetech.lang.StringHelper;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * Manage the link layer portion of the protocol, including:
 * commands, acks, sequencing and crc
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class UltraUltra2LinkLayerConnectionManager {

    private InputStream is;
    private OutputStream os;
    final private static byte[] COMMAND_INITIALIZE = new byte[]{
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) '@'
    };
    final private static byte[] COMMAND_INITIALIZE_ACKNOWLEDGEMENT = new byte[0];

    public UltraUltra2LinkLayerConnectionManager(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        initializeLinkLayer();
    }

    /**
     * @return application data packet that has passed validation (crc check etc.)
     **/
    public byte[] sendCommandToMeter(final byte[] command) {
        StopWatch sw = new StopWatch("sendCommandToMeter");

        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }

        buildAndSendCommandFrame(command);
        byte[] ackAndCommandResponse = readCommandResponseFromMeter();

        if (isValidFrameSet(ackAndCommandResponse, 0)) {
            byte[] applicationResponse = decodeApplicationResponse(ackAndCommandResponse);
            Logger.log(sw.getElapsedTimeMessage());
            return applicationResponse;
        } else {
            throw new IllegalArgumentException("Bad frame set");
        }
    }

    private void buildAndSendCommandFrame(final byte[] command) {
        byte[] commandFrame = command;
        Logger.log("commandFrame=", StringHelper.format(new String(commandFrame), "0x"));
        Logger.log("commandFrame=", new String(commandFrame));
        write(commandFrame);
    }

    private void initializeLinkLayer() {
        write(COMMAND_INITIALIZE);
        byte[] applicationResponse = readCommandResponseFromMeter();
        Logger.log("initializeLinkLayer()", StringHelper.format(new String(applicationResponse), "0x"));
        Logger.log("initializeLinkLayer()", new String(applicationResponse));
////        if ( !ArrayHelper.equals( applicationResponse, COMMAND_INITIALIZE_ACKNOWLEDGEMENT ) ) {
////            throw new RuntimeException( "Initialization not acknowledged" );
////        };
    }

    private void write(byte[] b) {
        try {
            for (int i = 0; i < b.length; i++) {
                os.write(b[i]);
                ThreadHelper.sleep(10);
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    /**
     * @return application data packet that has passed CRC checks and is extracted form link layer packet
     **/
    private byte[] readCommandResponseFromMeter() {
        byte[] output = readAllMeterData();
        Logger.log("readCommandResponseFromMeter", StringHelper.format(new String(output), "0x"));

        return output;
    }

    /**
     * @return data read from the meter
     */
    private byte[] readAllMeterData() {
        try {
            StopWatch sw = new StopWatch("readMeterData()");
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            // Convert to byte[] output stream
            int numberOfTimesContinued = 0;
            int c = 0;
            do {
                if (is.available() == 0) {
                    numberOfTimesContinued++;
                    if (numberOfTimesContinued > 30) {
                        Logger.log("readMeterData():break");
                        break;
                    } else {
                        Logger.log("readMeterData():continue");
                        ThreadHelper.sleep(500);
                        continue;
                    }
                }

                if (is.available() != 0) {
                    c = is.read();
                    baos.write(c);
                    numberOfTimesContinued = 0;
                }



            } while (c > -1);

            Logger.log("readMeterData()", sw.getElapsedTimeMessage());
            return baos.toByteArray();

        } catch (IOException ex) {
            Logger.log(ex);
        }
        return null;
    }

    private byte[] decodeApplicationResponse(byte[] ackAndCommandResponse) {
        // skip over ack frame
        // skip over control bytes
        // pull off crc
//////        int startOfData = 0;
//////        int dataLength = 8096;
//////        Logger.log( "startOfData", startOfData );
//////        Logger.log( "dataLength", dataLength );
//////        byte[] data = new byte[dataLength];
//////        for ( int i = 0; i < dataLength; i++ ) {
//////            data[i] = ackAndCommandResponse[startOfData + i];
//////        }

        return ackAndCommandResponse;
    }

    /**
     * @param buffer the frameBuffer which may contain up to two frames
     * [02 06 06 03 fc 41 02 0a 02 05 0f 00 00 03 4c 01]
     * @param offset, initially zero then called recursively to validate entire buffer of frames
     */
    private boolean isValidFrameSet(byte[] buffer, int offset) {
        boolean isValid = true;
        return isValid;
    }
}
