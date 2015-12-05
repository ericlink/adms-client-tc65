package net.diabetech.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Vector;
import javax.microedition.io.CommConnection;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import net.diabetech.lang.StringHelper;

import net.diabetech.microedition.io.ConnectionHelper;
import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.CalendarHelper;
import net.diabetech.util.ClockManager;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;
import net.diabetech.lang.ThreadHelper;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class GlucoMonStationaryTest extends MIDlet {
    // *****************************
    // lifecycle methods
    // *****************************
    public GlucoMonStationaryTest() {
        isDebug = "true".equalsIgnoreCase( getAppProperty( "isDebug" ) );
        Logger.setDebug(isDebug);
        Logger.log( "isDebug=",isDebug);
        Logger.log("GlucoMonStationary()");
        appVersion = getAppProperty( "MIDlet-Version" );
        Logger.log( "appVersion=", appVersion );
    }
    
    public void startApp() throws MIDletStateChangeException {
        Logger.log("startApp()");
        try {
            ThreadHelper.sleep( NUMBER_OF_SECONDS_TO_WAIT_BEFORE_APP_STARTUP );
            //getStartupMessagePayload();
            //transmitData("APP_STARTUP");
        } catch ( Throwable t ) {
            Logger.log( "startApp() Throwable: ", t.toString() );
        } finally {
            dataToTransmit = null;
        }
        run();
    }
    
    public void pauseApp() {
        Logger.log("pauseApp()");
        cleanupSerialPortResources();
        cleanupDataPostConnection();
    }
    
    public void destroyApp(boolean cond) {
        Logger.log("destroyApp",cond);
        cleanupSerialPortResources();
        cleanupDataPostConnection();
        notifyDestroyed();
    }
    
    
    /**
     * Main program logic and exception management
     ***/
    private void run() {
        while( true ) {
            try {
                ThreadHelper.sleep( MILLIS_TO_WAIT_BETWEEN_DATA_POLL_LOOPS );
                //acquireData();
                //calculateDelta();
                //transmitData(null);
                
                openSerialPort();
                wakeMeter();
            } catch ( Throwable t ) {
                Logger.log( "run() Throwable: ", t.toString() );
            } finally {
                cleanupSerialPortResources();
            }
        }
    }
    
    // *****************************
    // utility methods
    // *****************************
    
    /**
     * Create payload to send APP_VER:SIM_ID:CSQ:COPS
     **/
    private void getStartupMessagePayload() {
        final String DELIMITER = ":";
        StringBuffer payload = new StringBuffer();
        payload.append( Tc65Module.getSimId() );
        payload.append( DELIMITER );
        payload.append( Tc65Module.getSignalQuality() );
        payload.append( DELIMITER );
        payload.append( Tc65Module.getOperatorId() );
        dataToTransmit = payload.toString();
        Tc65Module.release();
    }
    
    private void acquireData() {
        try {
            medicalDeviceRawData = null;
            openSerialPort();
            queryMeter();
            medicalDeviceRawData = readMeterData();
            verifyData();
        } catch (Throwable ex) {
            medicalDeviceRawData =  null; // ensure medicalDeviceRawData discarded if there is any problem
            Logger.log( "acquireData()", ex );
        } finally {
            cleanupSerialPortResources();
        }
    }
    
    private void openSerialPort() throws IOException {
        serialPortConnection = (CommConnection)Connector.open(SERIAL_PORT_CONNECTION_PARAMETERS);
        Logger.log("CommConnection(", SERIAL_PORT_CONNECTION_PARAMETERS, ") opened");
        Logger.log("Real baud rate: ", serialPortConnection.getBaudRate());
        serialPortInputStream  = serialPortConnection.openInputStream();
        serialPortOutputStream = serialPortConnection.openOutputStream();
    }
    
    private void cleanupSerialPortResources() {
        Logger.log("cleanupSerialPortResources()");
        ConnectionHelper.close( serialPortInputStream );
        ConnectionHelper.close( serialPortOutputStream );
        ConnectionHelper.close( serialPortConnection );
    }
    
    public void queryMeter() throws Exception {
        Logger.log("queryMeter");
        wakeMeter();
        setMedicalDeviceClock();
        StopWatch sw = new StopWatch("DMP");
        write((int)'D');
        write((int)'M');
        write((int)'P');
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
    }
    
    private void wakeMeter() throws IOException {
        Logger.log( "UltraFamilyMeterProbe");
        StopWatch sw = new StopWatch("UltraFamilyMeterProbe ctor() + probe()");
        UltraFamilyMeterProbe probe
                = new UltraFamilyMeterProbe( serialPortInputStream, serialPortOutputStream );
        int meterType = probe.probe();
        
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log( "UltraFamilyMeterProbe Probe Complete. meterType",meterType);
        
        // just to show pc on mini for timing feel
        if ( meterType == UltraFamilyMeterProbe.ULTRA_MINI ) {
            Logger.log( "Mini wakeup");
            MiniLinkLayerConnectionManager llcm
                    = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
                    // Get meter serial number (makes PC display)
            byte[] applicationResponse = llcm.sendCommandToMeter(
//                    new byte[] {(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01}
                    new byte[] {
                    (byte)0x05 ,(byte)0x0B ,(byte)0x02 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
            }
                    );
            Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"),new String(applicationResponse));
        }
        
    }
    private void wakeMeterProbeBeta2() throws IOException {
        Logger.log( "UltraFamilyMeterProbe");
        StopWatch sw = new StopWatch("UltraFamilyMeterProbe ctor() + probe()");
        UltraFamilyMeterProbe probe
                = new UltraFamilyMeterProbe( serialPortInputStream, serialPortOutputStream );
        int meterType = probe.probe();
        
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log( "UltraFamilyMeterProbe Probe Complete. meterType",meterType);
        
        // just to show pc on mini for timing feel
        if ( meterType == UltraFamilyMeterProbe.ULTRA_MINI ) {
            Logger.log( "Mini wakeup");
            MiniLinkLayerConnectionManager llcm
                    = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
                    // Get meter serial number (makes PC display)
            byte[] applicationResponse = llcm.sendCommandToMeter(
//                    new byte[] {(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01}
                    new byte[] {
                    (byte)0x05 ,(byte)0x0B ,(byte)0x02 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00 ,(byte)0x00
            }
                    );
            Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"),new String(applicationResponse));
        }
        
    }
    private void wakeMeterProbeBeta() throws IOException {
        Logger.log( "UltraFamilyMeterProbe");
        StopWatch sw = new StopWatch("UltraFamilyMeterProbe ctor() + probe()");
        UltraFamilyMeterProbe probe
                = new UltraFamilyMeterProbe( serialPortInputStream, serialPortOutputStream );
        int meterType = probe.probe();
        
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log( "UltraFamilyMeterProbe Probe Complete. meterType",meterType);
        
        // just to show pc on mini for timing feel
        if ( meterType == UltraFamilyMeterProbe.ULTRA_MINI ) {
            Logger.log( "Mini wakeup");
            MiniLinkLayerConnectionManager llcm
                    = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
            byte[] applicationResponse = llcm.sendCommandToMeter(
                    new byte[] {(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01}
            );
            Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"));
        }
        
    }
    
    
    
    private void wakeMeterAndDMPUltraUltra2WorkingCleanly() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        Logger.log( "UltraUltra2 wakeup");
        UltraUltra2LinkLayerConnectionManager llcm
                = new UltraUltra2LinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        Logger.log( "Initialize complete (PC)" );
        byte[]applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x11, (byte)0x0D, (byte)'D',(byte)'M',(byte)'P'} );
        Logger.log("DMP",StringHelper.format(new String(applicationResponse), "0x"));
        Logger.log("DMP",new String(applicationResponse));
        
        Logger.log( sw.getElapsedTimeMessage() );
    }
    private void wakeMeterUltraMiniTestCases() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        
        
        Logger.log( "Mini wakeup");
        MiniLinkLayerConnectionManager llcm
                = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        
        Logger.log( "Ask for 501" );
//        byte[]applicationResponse = llcm.sendCommandToMeter(new byte[]
//        {(byte)0x02,(byte)0x0A,(byte)0x00,(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01,(byte)0x03,(byte)0x38,(byte)0xAA});
        byte[]applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01});
        Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"));
        
        
        llcm = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x0b,(byte)0x02,(byte)0x00, (byte)0x00, (byte)0x00, (byte)0x00, (byte)0x84, (byte)0x6a, (byte)0xe8, (byte)0x73, (byte)0x00 });
        Logger.log("wakeMeter() get serial number",StringHelper.format(new String(applicationResponse), "0x"));
        
        llcm = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x20,(byte)0x01,(byte)0xe0, (byte)0xed, (byte)0xc7, (byte)0x47 } );
        Logger.log("wakeMeter() set clock to 12:34:56 29 Feb 2008",StringHelper.format(new String(applicationResponse), "0x"));
        
        llcm = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0x01,(byte)0x00});
        Logger.log("wakeMeter() get reading two",StringHelper.format(new String(applicationResponse), "0x"));
        llcm = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0x02,(byte)0x00});
        Logger.log("wakeMeter() get reading three",StringHelper.format(new String(applicationResponse), "0x"));
        
        
        
        llcm = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0x00,(byte)0x00});
        Logger.log("wakeMeter() multiple readings: get reading one",StringHelper.format(new String(applicationResponse), "0x"));
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0x01,(byte)0x00});
        Logger.log("wakeMeter() multiple readings: get reading two",StringHelper.format(new String(applicationResponse), "0x"));
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0x02,(byte)0x00});
        Logger.log("wakeMeter() multiple readings: get reading three",StringHelper.format(new String(applicationResponse), "0x"));
        
        Logger.log( sw.getElapsedTimeMessage() );
    }
    private void wakeMeterWorkingLinkLayerAndAppCommandResponseExtraction() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        
        
        Logger.log( "Mini wakeup");
        MiniLinkLayerConnectionManager llcm
                = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        
        Logger.log( "Ask for 501" );
//        byte[]applicationResponse = llcm.sendCommandToMeter(new byte[]
//        {(byte)0x02,(byte)0x0A,(byte)0x00,(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01,(byte)0x03,(byte)0x38,(byte)0xAA});
        byte[]applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01});
        Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"));
        
        Logger.log( sw.getElapsedTimeMessage() );
    }
    
    private void wakeMeterMiniWorkingWithPCv2() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        
        
        Logger.log( "Mini wakeup");
        MiniLinkLayerConnectionManager llcm
                = new MiniLinkLayerConnectionManager( serialPortInputStream, serialPortOutputStream );
        byte[] applicationResponse = llcm.sendCommandToMeter(new byte[] {(byte)0x02,(byte)0x06,(byte)0x08,(byte)0x03,(byte)0xC2,(byte)0x62});
        Logger.log("wakeMeter() mini output",StringHelper.format(new String(applicationResponse), "0x"));
        
        Logger.log( "Ask for 501" );
        applicationResponse = llcm.sendCommandToMeter(new byte[]
        {(byte)0x02,(byte)0x0A,(byte)0x00,(byte)0x05,(byte)0x1F,(byte)0xF5,(byte)0x01,(byte)0x03,(byte)0x38,(byte)0xAA});
        Logger.log("wakeMeter() mini output get count",StringHelper.format(new String(applicationResponse), "0x"));
        
        Logger.log( sw.getElapsedTimeMessage() );
    }
    
    
    private void wakeMeterWorksForMiniAndDoesPc() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        Logger.log( "Mini wakeup");
        //Disconnect 02 06 08 03 C2 62
        write((int)0x02);
        write((int)0x06);
        write((int)0x08);
        write((int)0x03);
        write((int)0xC2);
        write((int)0x62);
//////        //ThreadHelper.sleep( 250 );
//////        //Ask for record 501 per manual
        
        //ThreadHelper.sleep( 10 );
        
        String output = readMeterData();
        Logger.log("wakeMeter() mini output",StringHelper.format(output, "0x"));
        write((int)0x02);
        write((int)0x0A);
        write((int)0x00);
        write((int)0x05);
        write((int)0x1F);
        write((int)0xF5);
        write((int)0x01);
        write((int)0x03);
        write((int)0x38);
        write((int)0xAA);
        output = readMeterData();
        Logger.log("wakeMeter() mini output get count",StringHelper.format(output, "0x"));
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
    }
    
    private void wakeMeterwakeMeterMiniV1NoPc() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        Logger.log( "Not Ultra/Ultra2, try Mini wakeup");
        /// try mini  FF FF FF 11 0D 44 4D 40                           ���..DM@
        /// 02 06 08 03 C2 62
        write((int)0xFF);
        write((int)0xFF);
        write((int)0xFF);
        write((int)0x11);
        write((int)0x0D);
        write((int)0x44);
        write((int)0x4D);
        write((int)0x40);
        cleanupSerialPortResources();
        openSerialPort();
        //02 06 08 03 C2 62
        write((int)0x02);
        write((int)0x06);
        write((int)0x08);
        write((int)0x03);
        write((int)0xC2);
        write((int)0x62);
        //ThreadHelper.sleep( 250 );
        //02 12 00 05 0B 02 00 00 00 00   ....�b..........
        write((int)0x02);
        write((int)0x12);
        write((int)0x00);
        write((int)0x05);
        write((int)0x0B);
        write((int)0x02);
        write((int)0x00);
        write((int)0x00);
        write((int)0x00);
        write((int)0x00);
        // 00 00 00 00 00 03 19 E7 02 06 06 03 CD 41 02 06   .......�....�A..
        write((int)0x00);
        write((int)0x00);
        write((int)0x00);
        write((int)0x00);
        write((int)0x00);
        write((int)0x03);
        write((int)0x19);
        write((int)0xE7);
        write((int)0x02);
        write((int)0x06);
        write((int)0x06);
        write((int)0x03);
        write((int)0xCD);
        write((int)0x41);
        write((int)0x02);
        write((int)0x06);
        
        
        // 0A 03 A0 04 02 09 00 05 0D 02 03 DA 71 02 06 07   ..�........�q...
        write((int)0x0A);
        write((int)0x03);
        write((int)0xA0);
        write((int)0x04);
        write((int)0x02);
        write((int)0x09);
        write((int)0x00);
        write((int)0x05);
        write((int)0x0D);
        write((int)0x02);
        write((int)0x03);
        write((int)0xDA);
        write((int)0x71);
        //       write((int)0x02);
        //     write((int)0x06);
        //   write((int)0x07);
        
        
        // 03 FC 72 02 12 03 05 0B 02 00 00 00 00 00 00 00   .�r.............
        // 00 00 03 BA 6A 02 06 04 03 AF 27 02 06 08 03 C2   ...�j....�'....�
        // 62 02 09 00 05 0D 02 03 DA 71 02 06 07 03 FC 72   b.......�q....�r
        // 02 0D 03 05 20 02 00 00 00 00 03 A8 4C 02 06 04   .... ......�L...
        // 03 AF 27 02 12 00 05 0B 02 00 00 00 00 00 00 00   .�'.............
        
        
        ThreadHelper.sleep( 250 );
        
        String output = readMeterData();
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log("wakeMeter() mini output",output);
    }
    
    private void wakeMeterMiniPcWorks() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        Logger.log( "Mini wakeup");
        //Disconnect 02 06 08 03 C2 62
        write((int)0x02);
        write((int)0x06);
        write((int)0x08);
        write((int)0x03);
        write((int)0xC2);
        write((int)0x62);
//////        //ThreadHelper.sleep( 250 );
//////        //Ask for record 501 per manual
        
        ThreadHelper.sleep( 100 );
        
        String output = readMeterData();
        Logger.log("wakeMeter() mini output",output);
        write((int)0x02);
        write((int)0x0A);
        write((int)0x00);
        write((int)0x05);
        write((int)0x1F);
        write((int)0xF5);
        write((int)0x01);
        write((int)0x03);
        write((int)0x38);
        write((int)0xAA);
        output = readMeterData();
        Logger.log("wakeMeter() mini output get count",output);
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log("wakeMeter() mini output",output);
    }
    private void wakeMeterUltraUltra2NoMiniPC() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        //PerLFS Software U/U2  11 0D 44 4D 53 0D 0D 11 0D 44 4D 53 0D 0D         ..DMS....DMS..
        write((int)0x11);
        write((int)0x0D);
        write((int)0x44);
        write((int)0x4D);
        write((int)0x53);
        write((int)0x0D);
        write((int)0x0D);
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        
        Logger.log("Resting after wakeUp");
        ThreadHelper.sleep( 250 );
        Logger.log("Clear serial line");
        String output = readMeterData(); // clear DM@ medicalDeviceRawData
        Logger.log("wakeMeter() output",output);
        if ( output.indexOf("S 0053\r\n") > -1 ) {
            //Include OD OA? 53 20 30 30 35 33 0D 0A                           S 0053..
            Logger.log( "Meter is Ultra or Ultra2");
        } else {
            Logger.log( "Not Ultra/Ultra2, try Mini wakeup");
            /// try mini  FF FF FF 11 0D 44 4D 40                           ���..DM@
            /// 02 06 08 03 C2 62
            write((int)0xFF);
            write((int)0xFF);
            write((int)0xFF);
            write((int)0x11);
            write((int)0x0D);
            write((int)0x44);
            write((int)0x4D);
            write((int)0x40);
            ThreadHelper.sleep( 250 );
            write((int)0x02);
            write((int)0x06);
            write((int)0x08);
            write((int)0x03);
            write((int)0xC2);
            write((int)0x62);
            ThreadHelper.sleep( 250 );
            output = readMeterData();
            Logger.log("wakeMeter() mini output",output);
            // 40 20 22 56 51 53 32 39 32 34 41 59 22 20 30 33   @ "VQS2924AY" 03
            // 30 39 0D 0A                                       09..
        }
    }
    private void wakeMeterDmsDms() throws IOException {
        StopWatch sw = new StopWatch("wakeMeter");
        //PerLFS Software U/U2  11 0D 44 4D 53 0D 0D 11 0D 44 4D 53 0D 0D         ..DMS....DMS..
        write((int)0x11);
        write((int)0x0D);
        write((int)0x44);
        write((int)0x4D);
        write((int)0x53);
        write((int)0x0D);
        write((int)0x0D);
        
        write((int)0x11);
        write((int)0x0D);
        write((int)0x44);
        write((int)0x4D);
        write((int)0x53);
        write((int)0x0D);
        write((int)0x0D);
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        
        Logger.log("Resting after wakeUp");
        ThreadHelper.sleep( 200 );
        Logger.log("Clear serial line");
        String output = readMeterData(); // clear DM@ medicalDeviceRawData
        Logger.log("wakeMeter() output",output);
    }
    private void wakeMeterOriginalInclU2() throws IOException {
        StopWatch sw = new StopWatch("DM@");
        // For One Touch Ultra,
        // Use DM@ instead of DM to turn on the meter
        // Calling DM 2x in a row hangs meter, DM@ is safe
        write((int)0x11);
        write((int)0x0D);
        write((int)'D');
        write((int)'M');
        write((int)'@');
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        
        // Ultra2 Spec is 20 sec,5 sec works reliably for Ultra
        Logger.log("Resting after DM@");
        ThreadHelper.sleep( 5 * 1000 );
        Logger.log("Clear serial line");
        String output = readMeterData(); // clear DM@ medicalDeviceRawData
        Logger.log("wakeMeter() output",output);
    }
    
    private void write(int c) throws IOException {
        serialPortOutputStream.write(c);
        //ThreadHelper.sleep( 30 );
    }
    
    /**
     * @return data read from the meter
     */
    private String readMeterData() throws IOException {
        StopWatch sw = new StopWatch("readMeterData()");
        StringBuffer sb = new StringBuffer();
        int numberOfTimesContinued = 0;
        int c = 0;
        do {
            if ( serialPortInputStream.available() == 0 ) {
                numberOfTimesContinued++;
                // protect against endless loop; we don't always shave more medicalDeviceRawData
                if (numberOfTimesContinued > 10) {
                    Logger.log("readMeterData():numberOfTimesContinued > 10");
                    break;
                } else {
                    Logger.log("readMeterData():don't block, continue");
                    ThreadHelper.sleep( 100 );
                    continue;
                }
            }
            c = serialPortInputStream.read();
            sb.append(String.valueOf((char)c));
            if ( serialPortInputStream.available() == 0 ) {
                Logger.log("wait for more data and check again");
                ThreadHelper.sleep( 500 );
                if ( serialPortInputStream.available() == 0 ) {
                    Logger.log("give up no more data");
                    break;
                }
            }
        } while (c > -1 );
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        Logger.log(medicalDeviceRawData);
        return sb.toString();
    }
    
    /**
     * Check for dataToTransmit at the 'blob' level - any new medicalDeviceRawData send all for now
     */
    private void calculateDelta() {
        dataToTransmit = null; // recalculating so reinitialize
        boolean haveNewData = medicalDeviceRawData != null && !previousData.contains(medicalDeviceRawData);
        if ( haveNewData ) {
            dataToTransmit = medicalDeviceRawData;
        }
    }
    
    /**
     * To save data volume, do all the work w/ server in one send.
     * Transmit data to the server,
     * collect time stamps to set local time on module,
     * check current software version
     * @param messageId null for default data message, otherwise value for different types of payloads
     **/
    public void transmitData(String messageId) throws IOException {
        Logger.log("transmitData()");
        try {
            if ( dataToTransmit == null ) {
                Logger.log("returning, no delta to send");
                return;
            }
            long clientTimeOnSend = System.currentTimeMillis();
            int rc = setupHeadersAndPostData(messageId);
            long clientTimeOnReceipt = System.currentTimeMillis();
            Logger.log("HTTP response: ",  rc );
            if ( rc == HttpConnection.HTTP_OK ) {
                recordSentDataForDeltaCalculation();
                Vector serverResponseElements = readAndParseServerResponse();
                setModuleClock(
                        Long.parseLong( (String)serverResponseElements.elementAt(1) ),
                        Long.parseLong( (String)serverResponseElements.elementAt(2) ),
                        clientTimeOnSend, clientTimeOnReceipt );
                updateApplication( (String)serverResponseElements.elementAt(0) );
            }
        } catch (Throwable t) {
            Logger.log("dataPostConnection problem, probably transient so keep on going", t );
        } finally {
            cleanupDataPostConnection();
        }
    }
    
    private int setupHeadersAndPostData(final String messageId) throws IOException {
        dataPostConnection = (HttpConnection)Connector.open(DATA_POST_CONNECTION_PARAMETERS);
        dataPostConnection.setRequestMethod(HttpConnection.POST);
        dataPostConnection.setRequestProperty("IMEI",Tc65Module.getImei());
        // app version i sneeded every time since module may not be
        // restarted for long periods of time, and we want to do otap based on it
        dataPostConnection.setRequestProperty("APP_VER",appVersion);
        dataPostConnection.setRequestProperty( "Authorization", "Basic UTR0RlBpIXJwNlpWKW96djptIUhQcmU4V0pCQGh2Zm5K" );
        if ( messageId != null ) {
            dataPostConnection.setRequestProperty("MSG_ID", messageId);
        }
        dataPostOutput = dataPostConnection.openOutputStream();
        dataPostOutput.write(dataToTransmit.getBytes());
        dataPostOutput.flush();
        int rc = dataPostConnection.getResponseCode();
        return rc;
    }
    
    private void setModuleClock(
            final long serverTimeOnReceipt,
            final long serverTimeOnSend,
            final long clientTimeOnSend,
            final long clientTimeOnReceipt
            ) throws NumberFormatException {
        Logger.log( "clientTimeOnSend",clientTimeOnSend);
        Logger.log( "serverTimeOnReceipt",serverTimeOnReceipt);
        Logger.log( "serverTimeOnSend",serverTimeOnSend);
        Logger.log( "clientTimeOnReceipt",clientTimeOnReceipt);
        if ( serverTimeOnReceipt == 0 || serverTimeOnSend == 0 ) {
            Logger.log( "Can't set time, no info" );
            return;
        }
        clockManager = new ClockManager( clientTimeOnSend, clientTimeOnReceipt,
                serverTimeOnReceipt, serverTimeOnSend );
    }
    
    private Vector readAndParseServerResponse() throws IOException {
        dataPostInput = dataPostConnection.openInputStream();
        StringBuffer serverResponseBuffer = new StringBuffer();
        int ch = 0;
        while( (ch = dataPostInput.read()) != -1 ) {
            serverResponseBuffer.append((char)ch);
        }
        String serverResponse = serverResponseBuffer.toString();
        Logger.log( "serverResponse", serverResponse );
        
        Vector response = StringHelper.split( serverResponse, ":" );
        if ( response.size() != DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE ) {
            Logger.log( "Problem with server response, using no-op values" );
            response.removeAllElements();
            for ( int i = 0; i < DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE; i++ ) {
                response.addElement( "0" );
            }
        }
        
        return response;
    }
    
    /**
     * Successfully sent data is recorded here so it can be considered
     * when calculating the delta to send next time
     **/
    private void recordSentDataForDeltaCalculation() {
        previousData.addElement(medicalDeviceRawData);
        if ( previousData.size() > NUMBER_OF_PREVIOUS_DATA_SETS_TO_RETAIN ) {
            previousData.removeElementAt(0);
        }
    }
    
    private void cleanupDataPostConnection() {
        ConnectionHelper.close( dataPostInput );
        ConnectionHelper.close( dataPostOutput );
        ConnectionHelper.close( dataPostConnection );
    }
    
    private void verifyData() {
        if (
                medicalDeviceRawData == null //protect against null pointer on subsequent checks
                || medicalDeviceRawData.length() < MINIMUM_DATA_LENGTH
                || !medicalDeviceRawData.startsWith(VALID_HEADER_RECORD_STARTS_WITH)
                ) {
            Logger.log( "No data or bad data, discarding.", medicalDeviceRawData );
            medicalDeviceRawData = null;
        }
    }
    
    private void updateApplication( final String interfaceAppVersion ) {
        Logger.log( "interfaceAppVersion", interfaceAppVersion );
        if ( interfaceAppVersion == null || interfaceAppVersion.equals( "0" ) ) {
            Logger.log( "No valid app version to work with for otap" );
            return;
        }
        
        if ( !this.appVersion.equalsIgnoreCase( interfaceAppVersion ) ) {
            Logger.log( "doOtap" );
//            AtCommandManager.send( "\rAT^SJOTAP=\"Jd3jK9u\",\"http://gdyx.diabetech.net/st/otap/GlucoMonStationary.jad\",\"a:/ota\",\"Q4tFPi!rp6ZV)ozv\",\"m!HPre8WJB@hvfnJ\",\"gprs\",\"proxy\",,,,\"http://gdyx.diabetech.net/st/otap/notify/\"\r" );
//            AtCommandManager.send( "\rAT^SJOTAP\r" );
        }
    }
    
    private void setMedicalDeviceClock() throws IOException {
        if ( clockManager == null ) {
            Logger.log("clockManager not initialized, can not set time" );
            return;
        }
        StopWatch sw = new StopWatch("DMT");
        
        // build up command based on clockManager time
        Calendar currentTime = clockManager.getManagedCalendar();
        Logger.log( currentTime );
        
        StringBuffer command = new StringBuffer();
        command.append( "DMT" );
        command.append( CalendarHelper.getFormattedMonth( currentTime ) );
        command.append( "/" );
        command.append( CalendarHelper.getFormattedDayOfMonth( currentTime ) );
        command.append( "/" );
        command.append( CalendarHelper.getFormattedYear( currentTime ) );
        command.append( " " );
        command.append( CalendarHelper.getFormattedHourOfDay( currentTime ) );
        command.append( ":" );
        command.append( CalendarHelper.getFormattedMinute( currentTime ) );
        command.append( "\r" );
        Logger.log( "command",command);
        // command as string method
        char[] cmd = command.toString().toCharArray();//"DMT12/31/04 14:30\r".toCharArray();
        for ( int i = 0; i < cmd.length; i ++ ) {
            write((int)cmd[i]);
        }
        sw.stop();
        Logger.log( sw.getElapsedTimeMessage() );
        
        Logger.log("Resting after DMT");
        ThreadHelper.sleep( 5 * 1000 );
        Logger.log("Clear serial line");
        String response = readMeterData(); // clear returned time
        Logger.log( "Set time response", response );
    }
    
    // member variables
    private long NUMBER_OF_SECONDS_TO_WAIT_BEFORE_APP_STARTUP = 1000 * 5;
    private String appVersion = null; //comes from jad MIDlet-Version
    private boolean isDebug = false;    //comes from jad isDebug
    private ClockManager clockManager = null;
    private String medicalDeviceRawData = null;
    private String dataToTransmit = null;
    private Vector previousData = new Vector();
    private final static long MILLIS_TO_WAIT_BETWEEN_DATA_POLL_LOOPS = 500;
    private final static int NUMBER_OF_PREVIOUS_DATA_SETS_TO_RETAIN = 5;
    
    // http post
    private InputStream dataPostInput = null;
    private OutputStream dataPostOutput = null;
    private HttpConnection dataPostConnection = null;
    
    // http post connection Parameters
    private final static int DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE = 3;
    private final static String DATA_POST_PROTOCOL = "http://";
    private final static String DATA_POST_HOST = "gdyx.diabetech.net";
    private final static String DATA_POST_PORT = "80";
    private final static String DATA_POST_FILE = "st/data/";
    private final static String DATA_POST_CONNECTION_PROFILE
            = "bearer_type=gprs;access_point=c1.korem2m.com;username=;password=";
    private final static String DATA_POST_CONNECTION_PARAMETERS
            = DATA_POST_PROTOCOL
            + DATA_POST_HOST
            + ":"
            + DATA_POST_PORT
            + "/"
            + DATA_POST_FILE
            + ";"
            + DATA_POST_CONNECTION_PROFILE;
    
    private CommConnection  serialPortConnection = null;
    private InputStream     serialPortInputStream = null;
    private OutputStream    serialPortOutputStream= null;
    private final static String SERIAL_PORT_CONNECTION_PARAMETERS
            = "comm:com0;blocking=on;baudrate=9600;stopbits=1;parity=none;autocts=on;autorts=on";
    
    
    // One Touch Ultra
    private final static int MINIMUM_DATA_LENGTH = 31; //header record length Ultra 31 + \n
    private final static String VALID_HEADER_RECORD_STARTS_WITH = "P ";
    
    
}
