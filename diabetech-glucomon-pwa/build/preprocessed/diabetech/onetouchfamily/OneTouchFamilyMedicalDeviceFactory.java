package net.diabetech.onetouchfamily;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import net.diabetech.glucomon.MedicalDevice;
import net.diabetech.glucomon.MedicalDeviceFactory;
import net.diabetech.microedition.io.ConnectionHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class OneTouchFamilyMedicalDeviceFactory implements MedicalDeviceFactory {

    public OneTouchFamilyMedicalDeviceFactory() {
        Logger.log("OneTouchFamilyMedicalDeviceFactory()");
    }

    public MedicalDevice create() {
        try {
            Logger.log("OneTouchFamilyMedicalDeviceFactory.create()");
            openSerialPort();
            int meterType = probe();
            switch (meterType) {
                case OneTouchFamilyProbe.ULTRA:
                    Logger.log("Ultra Found");
                    return new UltraMedicalDevice(new UltraUltra2LinkLayer(input, output));
                case OneTouchFamilyProbe.ULTRA2:
                    Logger.log("Ultra2 Found");
                    return new Ultra2MedicalDevice(new UltraUltra2LinkLayer(input, output));
                default:
                    return null;
            }
        } catch (Throwable t) {
            Logger.log("OneTouchFamilyMedicalDeviceFactory.create()", t);
            return null;
        }
    }

    public void destroy() {
        Logger.log("OneTouchFamilyMedicalDeviceFactory.destroy()");
        closeSerialPort();
    }

    private int probe() throws IOException {
        StopWatch sw = new StopWatch("OneTouchFamilyMedicalDeviceFactory.create(): probe meter");
        OneTouchFamilyProbe probe = new OneTouchFamilyProbe(input, output);
        int meterType = probe.probe();
        Logger.log(sw.getElapsedTimeMessage());
        Logger.log("UltraFamilyMeterProbe Probe Complete. meterType", meterType);
        return meterType;
    }

    private void openSerialPort() throws IOException {
        Logger.log("openSerialPort()");
        closeSerialPort();
        connection = (CommConnection) Connector.open(SERIAL_PORT_CONNECTION_PARAMETERS);
        Logger.log("CommConnection(", SERIAL_PORT_CONNECTION_PARAMETERS, ") opened");
        Logger.log("Real baud rate: ", connection.getBaudRate());
        input = connection.openInputStream();
        output = connection.openOutputStream();
    }

    private void closeSerialPort() {
        Logger.log("closeSerialPort()");
        ConnectionHelper.close(input);
        ConnectionHelper.close(output);
        ConnectionHelper.close(connection);
    }
    private CommConnection connection;
    private InputStream input;
    private OutputStream output;
    private final static String SERIAL_PORT_CONNECTION_PARAMETERS = "comm:com0;blocking=on;baudrate=9600;stopbits=1;parity=none;autocts=on;autorts=on";
}
