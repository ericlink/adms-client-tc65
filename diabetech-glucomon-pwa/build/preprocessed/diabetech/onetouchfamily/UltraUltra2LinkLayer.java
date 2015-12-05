package net.diabetech.onetouchfamily;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * LinkLayer for ultras is minimal, they really don't implement any control protocol
 * @author elink
 * Manage the link layer portion of the protocol, including:
 * commands, acks, sequencing and crc
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class UltraUltra2LinkLayer {

    UltraUltra2LinkLayer(InputStream is, OutputStream os) {
        this.is = is;
        this.os = os;
        initializeLinkLayer();
    }

    /**
     * @return application data packet that has passed validation (crc check etc.)
     **/
    byte[] sendCommandToMeter(final byte[] command) {
        Logger.log("sendCommandToMeter()");
        StopWatch sw = new StopWatch("sendCommandToMeter");

        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }

        try {
            buildAndSendCommandFrame(command);
            byte[] ackAndCommandResponse = readCommandResponseFromMeter();
            Logger.logByteArray("ackAndCommandResponse", ackAndCommandResponse);
            if (isValidFrameSet(ackAndCommandResponse, 0)) {
                byte[] applicationResponse = decodeApplicationResponse(ackAndCommandResponse);
                return applicationResponse;
            } else {
                throw new IllegalArgumentException("Bad frame set");
            }
        } finally {
            Logger.log(sw.getElapsedTimeMessage());
        }
    }

    private void buildAndSendCommandFrame(final byte[] command) {
        byte[] commandFrame = command;
        Logger.logByteArray("commandFrame", commandFrame, true);
        write(commandFrame);
    }

    private void initializeLinkLayer() {
        write(COMMAND_INITIALIZE);
        byte[] applicationResponse = readCommandResponseFromMeter();
        Logger.logByteArray("initializeLinkLayer()", applicationResponse);
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
     * @return application data packet that has passed the link layer
     * CRC checks and is extracted from link layer packet
     **/
    private byte[] readCommandResponseFromMeter() {
        return readMeterData();
    }

    private byte[] decodeApplicationResponse(byte[] ackAndCommandResponse) {
        return ackAndCommandResponse;
    }

    private boolean isValidFrameSet(byte[] buffer, int offset) {
        return true;
    }

    private byte[] readMeterData() {
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
            return baos.toByteArray();
        } catch (IOException ex) {
            Logger.log(ex);
        }
        return null;
    }
    private InputStream is;
    private OutputStream os;
    private ByteArrayOutputStream baos = new ByteArrayOutputStream(32 * 1024);
    private final byte[] readBuffer = new byte[1024];
    private static final int INITIAL_SLEEP_WAIT_FOR_METER_TO_BECOME_READY = 500;
    private static final int MAX_RETRIES = 5;
    private static final int RETRY_SLEEP = 100;
    private static final byte[] COMMAND_INITIALIZE = new byte[]{
        (byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) '@'
    };
    //final private static byte[] COMMAND_INITIALIZE_ACKNOWLEDGEMENT = new byte[0];
}
