/*
 * MiniLinkLayerConnectionManager.java
 *
 * Created on April 30, 2008, 4:15 PM
 *
 * Manage the link layer portion of the protocol, including:
 * commands, acks, sequencing and crc
 *
 */
package net.diabetech.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.diabetech.lang.ArrayHelper;
import net.diabetech.lang.StringHelper;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.CrcHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class MiniLinkLayerConnectionManager {

    private InputStream is;
    private OutputStream os;
    private int transmissionCounter;
    private long transmissionTimer;
    private byte linkByte;
    final private static byte START_OF_MESSAGE_INDICATOR = 0x02;
    final private static byte END_OF_MESSAGE_INDICATOR = 0x03;
    final private static byte LINK_CONTROL_BYTE_MASK_MORE = 0x10;
    final private static byte LINK_CONTROL_BYTE_MASK_DISCONNECT = 0x08;
    final private static byte LINK_CONTROL_BYTE_MASK_ACKNOWLEDGE = 0x04;
    final private static byte LINK_CONTROL_BYTE_MASK_EXPECTED_RECEIVE = 0x00;
    final private static byte LINK_CONTROL_BYTE_MASK_EXPECTED_SEND = 0x00;
    final private static int COMMAND_FRAME_CONTROL_BYTES_LENGTH = 6;
    final private static int MESSAGE_FRAME_INDEX_START_INDICATOR = 0;
    final private static int MESSAGE_FRAME_INDEX_LENGTH = 1;
    final private static int MESSAGE_FRAME_INDEX_LINK_BYTE = 2;
    final private static int MESSAGE_FRAME_INDEX_DATA_START = 3;
    final private static int MESSAGE_FRAME_OFFSET_END_INDICATOR = 1;
    final private static int MESSAGE_FRAME_OFFSET_CRC_LOW_BYTE = 2;
    final private static int MESSAGE_FRAME_OFFSET_CRC_HIGH_BYTE = 3;
    final private static int MESSAGE_FRAME_NEGATIVE_OFFSET_END_OF_MESSAGE_INDICATOR = -3;
    final private static int MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_LOW_BYTE = -2;
    final private static int MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_HIGH_BYTE = -1;
    final private static byte[] COMMAND_INITIALIZE_DISCONNECT = new byte[]{(byte) 0x02, (byte) 0x06, (byte) 0x08, (byte) 0x03, (byte) 0xC2, (byte) 0x62};
    final private static byte[] COMMAND_INITIALIZE_ACKNOWLEDGEMENT = new byte[]{(byte) 0x02, (byte) 0x06, (byte) 0x0c, (byte) 0x03, (byte) 0x06, (byte) 0xae};
    final private static int ACKNOWLEDGEMNET_FIXED_LENGTH = 6;
    final private static int MESSAGE_FRAME_INDEX_ACKNOWLEDGEMENT_END_INDICATOR = 3;

    public MiniLinkLayerConnectionManager(InputStream is, OutputStream os) throws IOException {
        this.is = is;
        this.os = os;
        initializeLinkLayer();
    }

    /**
     * @return application data packet that has passed validation (crc check etc.)
     **/
    public byte[] sendCommandToMeter(final byte[] command) throws IOException {
        StopWatch sw = new StopWatch("sendCommandToMeter");

        if (command == null) {
            throw new IllegalArgumentException("Command must not be null");
        }

        buildAndSendCommandFrame(command);
        byte[] ackAndCommandResponse = readCommandResponseFromMeter();

        if (isValidFrameSet(ackAndCommandResponse, 0)) {
            decodeAcknowledgement(ackAndCommandResponse);
            byte[] applicationResponse = decodeApplicationResponse(ackAndCommandResponse);
            sendAcknowledgementToMeter((byte) 0);
            Logger.log(sw.getElapsedTimeMessage());
            return applicationResponse;
        } else {
            throw new IllegalArgumentException("Bad frame set");
        }
    }

    private void buildAndSendCommandFrame(final byte[] command) {
        byte[] commandFrame = new byte[COMMAND_FRAME_CONTROL_BYTES_LENGTH + command.length];
        commandFrame[MESSAGE_FRAME_INDEX_START_INDICATOR] = START_OF_MESSAGE_INDICATOR;
        commandFrame[MESSAGE_FRAME_INDEX_LENGTH] = (byte) commandFrame.length;
        commandFrame[MESSAGE_FRAME_INDEX_LINK_BYTE] = linkByte;
        for (int i = 0; i < command.length; i++) {
            commandFrame[MESSAGE_FRAME_INDEX_DATA_START + i] = command[i];
        }
        commandFrame[MESSAGE_FRAME_INDEX_LINK_BYTE + command.length + MESSAGE_FRAME_OFFSET_END_INDICATOR] = END_OF_MESSAGE_INDICATOR;
        int crc = CrcHelper.calculateCrc16(commandFrame, 0, commandFrame.length - 2);
        commandFrame[MESSAGE_FRAME_INDEX_LINK_BYTE + command.length + MESSAGE_FRAME_OFFSET_CRC_LOW_BYTE] = (byte) crc;
        commandFrame[MESSAGE_FRAME_INDEX_LINK_BYTE + command.length + MESSAGE_FRAME_OFFSET_CRC_HIGH_BYTE] = (byte) (crc >> 8);
        Logger.log("commandFrame=", StringHelper.format(new String(commandFrame), "0x"));
        write(commandFrame);
    }

    private void initializeLinkLayer() throws IOException {
        // what about timeout timer, E/S flags?
        // should these build up messages also like send command?
        // set link byte on write/read/etc
        write(COMMAND_INITIALIZE_DISCONNECT);
        //for readAllMeterData02
        //ThreadHelper.sleep(100); //req'd or response doesn't show up for read all
        byte[] applicationResponse = readCommandResponseFromMeter();
        Logger.log("initializeLinkLayer()", StringHelper.format(new String(applicationResponse), "0x"));
        if (!ArrayHelper.equals(applicationResponse, COMMAND_INITIALIZE_ACKNOWLEDGEMENT)) {
            throw new RuntimeException("Initialization not acknowledged");
        }
        ;
    }

    private void write(byte[] b) {
        try {
            for (int i = 0; i < b.length; i++) {
                os.write(b[i]);
            }
        } catch (Exception e) {
            Logger.log(e);
        }
    }

    /**
     * @return application data packet that has passed CRC checks and is extracted form link layer packet
     **/
    private byte[] readCommandResponseFromMeter() throws IOException {
        byte[] output = readAllMeterData();
        Logger.log("readCommandResponseFromMeter", StringHelper.format(new String(output), "0x"));

        return output;
    }

    private void sendAcknowledgementToMeter(byte acknowledgementType) {
        linkByte = 0x06; //check for correct E/S flags
        buildAndSendCommandFrame(new byte[0]);
    }

    private byte decodeLinkControlByteAction(byte linkControlByte) {
        // if disc else ack else disconnect

        return 0;
    }

    private byte getLinkControlByteExpectedReceive(byte linkControlByte) {
        // if disc else ack else disconnect

        return 0;
    }

    private byte getLinkControlByteExpectedSend(byte linkControlByte) {
        // if disc else ack else disconnect

        return 0;
    }

    private byte[] readAllMeterData() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
        for (int i = 0; i < 20; i++) {
            if (is.available() == 0) {
                ThreadHelper.sleep(10);
            } else {
                while (is.available() > 0) {
                    //Logger.log("readAllMeterData():read",sw.getElapsedTimeMessage());
                    baos.write(is.read());
                    //Logger.log("readAllMeterData():is.available",sw.getElapsedTimeMessage());
                }
            }
        }
        return baos.toByteArray();
    }

    /**
     * @return data read from the meter
     */
    private byte[] readAllMeterData01() {
        try {
            StopWatch sw = new StopWatch("readMeterData()");
            ByteArrayOutputStream baos = new ByteArrayOutputStream(128);
            // Convert to byte[] output stream
            int numberOfTimesContinued = 0;
            int c = 0;
            do {
                if (is.available() == 0) {
                    numberOfTimesContinued++;
                    if (numberOfTimesContinued > 5) {
                        Logger.log("readMeterData():break");
                        break;
                    } else {
                        Logger.log("readMeterData():continue");
                        ThreadHelper.sleep(5);
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

    private void decodeAcknowledgement(byte[] ackAndCommandResponse) {
        //[02 06 06 03 fc 41 02 0a 02 05 0f 00 00 03 4c 01 ]
        // check link byte (disconnect?)
        // set E/S flags
    }

    private byte[] decodeApplicationResponse(byte[] ackAndCommandResponse) {
        // skip over ack frame
        // skip over control bytes
        // pull off crc
        int startOfData = COMMAND_FRAME_CONTROL_BYTES_LENGTH + MESSAGE_FRAME_INDEX_DATA_START;
        int dataLength = ackAndCommandResponse[COMMAND_FRAME_CONTROL_BYTES_LENGTH + MESSAGE_FRAME_INDEX_LENGTH] - COMMAND_FRAME_CONTROL_BYTES_LENGTH;
        Logger.log("startOfData", startOfData);
        Logger.log("dataLength", dataLength);
        byte[] data = new byte[dataLength];
        for (int i = 0; i < dataLength; i++) {
            data[i] = ackAndCommandResponse[startOfData + i];
        }

        return data;
    }

    /**
     * @param buffer the frameBuffer which may contain up to two frames
     * [02 06 06 03 fc 41 02 0a 02 05 0f 00 00 03 4c 01]
     * @param offset, initially zero then called recursively to validate entire buffer of frames
     */
    private boolean isValidFrameSet(byte[] buffer, int offset) {
        boolean isValid = true;

        if (buffer.length - offset < ACKNOWLEDGEMNET_FIXED_LENGTH) {
            Logger.log("< minimum rquired framelength(" + (buffer.length - offset) + ")");
            return false;
        }
        if (buffer[offset + MESSAGE_FRAME_INDEX_START_INDICATOR] != START_OF_MESSAGE_INDICATOR) {
            Logger.log("Start of message indicator not correct");
            return false;
        }

        int frameLength = buffer[offset + MESSAGE_FRAME_INDEX_LENGTH];

        if (offset + frameLength - 1 > buffer.length + offset - 1) {
            Logger.log("Length exceeds buffer size" + buffer.length + offset + ")");
            return false;
        }

        if (buffer[offset + frameLength + MESSAGE_FRAME_NEGATIVE_OFFSET_END_OF_MESSAGE_INDICATOR] != END_OF_MESSAGE_INDICATOR) {
            Logger.log("End of message indicator not correct");
            return false;
        }

        int crc = CrcHelper.calculateCrc16(buffer, offset, frameLength - 2);
        Logger.log("calculated CRC from frame", crc);
        Logger.log("offset", offset);
        Logger.log("Length", frameLength - 2);
        Logger.log("Low byte from frame", (byte) buffer[offset + frameLength + MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_LOW_BYTE]);
        Logger.log("Low byte calced", (byte) crc);
        Logger.log("High byte from frame", (byte) buffer[offset + frameLength + MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_HIGH_BYTE]);
        Logger.log("High byte from calc", (byte) (crc >> 8));
        if (buffer[offset + frameLength + MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_LOW_BYTE] != (byte) crc ||
                buffer[offset + frameLength + MESSAGE_FRAME_NEGATIVE_OFFSET_CRC_HIGH_BYTE] != (byte) (crc >> 8)) {
            Logger.log("Incorrect crc" + buffer.length + ")");
            return false;
        }

        if (offset == 0 && buffer.length > ACKNOWLEDGEMNET_FIXED_LENGTH) {
            Logger.log("validate the second frame (application data frame)");
            isValid = isValidFrameSet(buffer, frameLength);
        }

        return isValid;
    }
}
