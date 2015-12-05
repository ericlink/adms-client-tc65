package net.diabetech.onetouchfamily;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import net.diabetech.glucomon.MedicalDevice;
import net.diabetech.microedition.io.ConnectionHelper;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public abstract class AbstractOneTouchMedicalDevice implements MedicalDevice {

    AbstractOneTouchMedicalDevice() {
    }

    public void destroy() {
        closeSerialPort();
    }

    protected void openSerialPort() throws IOException {
        serialPortConnection = (CommConnection) Connector.open(SERIAL_PORT_CONNECTION_PARAMETERS);
        Logger.log("CommConnection opened", SERIAL_PORT_CONNECTION_PARAMETERS);
        Logger.log("Real baud rate", serialPortConnection.getBaudRate());
        serialPortInputStream = serialPortConnection.openInputStream();
        serialPortOutputStream = serialPortConnection.openOutputStream();
    }

    private void closeSerialPort() {
        Logger.log("closeSerialPort()");
        ConnectionHelper.close(serialPortInputStream);
        ConnectionHelper.close(serialPortOutputStream);
        ConnectionHelper.close(serialPortConnection);
    }
    private CommConnection serialPortConnection = null;
    private InputStream serialPortInputStream = null;
    private OutputStream serialPortOutputStream = null;
    private final static String SERIAL_PORT_CONNECTION_PARAMETERS = "comm:com0;blocking=on;baudrate=9600;stopbits=1;parity=none;autocts=on;autorts=on";
    // One Touch Ultra
    private final static int MINIMUM_DATA_LENGTH = 31; //header record length Ultra 31 + \n
    private final static String VALID_HEADER_RECORD_STARTS_WITH = "P ";
}
