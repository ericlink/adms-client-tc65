package net.diabetech.glucomon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;
import net.diabetech.lang.StringHelper;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.microedition.io.ConnectionHelper;
import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2011 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class MessageManager {

    MessageManager(String appVersion, HostManager hostManager) {
        this.appVersion = appVersion;
        this.hostManager = hostManager;
        this.alternateDataPostUri = null;
    }

    MessageManager(String appVersion, HostManager hostManager, String alternateDataPostUri) {
        this.appVersion = appVersion;
        this.hostManager = hostManager;
        this.alternateDataPostUri = alternateDataPostUri;
    }

    void destroy() {
        Logger.log("destroy()");
        ConnectionHelper.close(dataPostInput);
        ConnectionHelper.close(dataPostOutput);
        ConnectionHelper.close(dataPostConnection);
        //allow time for connection to clean up GPRS profile (timing is network dependent)
        ThreadHelper.sleep(5000);
    }

    void reset() {
        //reset response values
        targetAppVersion = null;
        serverTimeOnReceipt = 0;
        serverTimeOnSend = 0;
        rebootTime = 0;
        clientTimeOnSend = 0;
        clientTimeOnReceipt = 0;
    }

    /**
     * Create payload to send APP_VER:SIM_ID:CSQ:COPS:MEDICAL_DEVICE_FACTORY
     **/
    void sendStartupMessage(String medicalDeviceFactoryClassName,
            boolean sendStartupMessage,
            boolean shutdownAfterOnePass) throws IOException {
        final String DELIMITER = ":";
        StringBuffer payload = new StringBuffer();
        payload.append(Tc65Module.getSimId());
        payload.append(DELIMITER);
        payload.append(Tc65Module.getSignalQuality());
        payload.append(DELIMITER);
        payload.append(Tc65Module.getOperatorId());
        payload.append(DELIMITER);
        payload.append(medicalDeviceFactoryClassName);
        payload.append(DELIMITER);
        payload.append(appVersion);
        payload.append(DELIMITER);
        payload.append(sendStartupMessage);
        payload.append(DELIMITER);
        payload.append(shutdownAfterOnePass);
        payload.append(DELIMITER);
        payload.append(Tc65Module.getImei());
        payload.append(DELIMITER);
        payload.append(hostManager.getApn());
        byte[] startupMessage = payload.toString().getBytes();
        transmitData("APP_STARTUP", startupMessage);
    }

    void sendDataMessage(byte[] message) throws IOException {
        transmitData(null, message);
    }

    /**
     * To save data volume, do all the work w/ server in one send.
     * Transmit data to the server,
     * collect time stamps to set local time on module,
     * check current software version
     * @param messageId null for default data message, otherwise value for different types of payloads
     **/
    private void transmitData(String messageId, byte[] message) throws IOException {
        Logger.log("transmitData()");
        try {
            boolean sentOk = false;
            // try to send up to RETRIES times
            // (especially important w/ one pass option)
            final int RETRIES = 3;
            for (int i = 0; i < RETRIES; i++) {
                try {
                    destroy();
                    reset();
                    if (message == null) {
                        throw new IllegalArgumentException("Message is null");
                    }
                    setupHeaders(messageId);
                    sentOk = writeMessage(message);
                    if (sentOk) {
                        break;
                    }
                } catch (Throwable t) {
                    // control flow with retry loop and sentOk flag
                    Logger.log("xmit attempt=" + i, t.toString());
                }
            }
            if (!sentOk) {
                throw new RuntimeException("Failed to send data");
            }
            String serverResponse = readResponse();
            parseResponse(serverResponse);
        } finally {
            destroy();
        }
    }

    private boolean writeMessage(final byte[] message) throws IOException, IOException {
        Logger.log("writeMessage()");
        clientTimeOnSend = System.currentTimeMillis();
        dataPostOutput = dataPostConnection.openOutputStream();
        dataPostOutput.write(message);
        dataPostOutput.flush();
        int rc = dataPostConnection.getResponseCode();
        clientTimeOnReceipt = System.currentTimeMillis();
        Logger.log("HTTP response", rc);
        return HttpConnection.HTTP_OK == rc;
    }

    private void setupHeaders(final String messageId) throws IOException {
        Logger.log("setupHeaders()");
        String dataPostUri = alternateDataPostUri != null ? alternateDataPostUri : hostManager.getDataPostUri();
        dataPostConnection = (HttpConnection) Connector.open(dataPostUri);
        dataPostConnection.setRequestMethod(HttpConnection.POST);
        dataPostConnection.setRequestProperty("IMEI", Tc65Module.getImei());
        // app version is needed every time since module may not be
        // restarted for long periods of time, and we want to do otap based on it
        dataPostConnection.setRequestProperty("APP_VER", appVersion);
        dataPostConnection.setRequestProperty("Authorization", "Basic UTR0RlBpIXJwNlpWKW96djptIUhQcmU4V0pCQGh2Zm5K");
        if (messageId != null) {
            dataPostConnection.setRequestProperty("MSG_ID", messageId);
        }
    }

    private String readResponse() throws IOException {
        Logger.log("readResponse()");
        dataPostInput = dataPostConnection.openInputStream();
        StringBuffer serverResponseBuffer = new StringBuffer();
        int ch = 0;
        while ((ch = dataPostInput.read()) != -1) {
            serverResponseBuffer.append((char) ch);
        }
        String serverResponse = serverResponseBuffer.toString();
        Logger.log("serverResponse", serverResponse);
        return serverResponse;
    }

    private void parseResponse(final String serverResponse) throws IllegalStateException {
        Logger.log("parseResponse()");
        Vector response = StringHelper.split(serverResponse, ":");
        if (response.size() != DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE) {
            Logger.log("Problem with server response, using no-op values");
            response.removeAllElements();
            for (int i = 0; i < DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE; i++) {
                response.addElement("0");
            }
        }
        targetAppVersion = (String) response.elementAt(0);
        serverTimeOnReceipt = Long.parseLong((String) response.elementAt(1));
        serverTimeOnSend = Long.parseLong((String) response.elementAt(2));
// v3 add rebootTime
//        rebootTime = 0L;//Long.parseLong( (String)response.elementAt(3) );
    }

    String getTargetAppVersion() {
        return targetAppVersion;
    }

    long getServerTimeOnReceipt() {
        return serverTimeOnReceipt;
    }

    long getServerTimeOnSend() {
        return serverTimeOnSend;
    }

    long getRebootTime() {
        return rebootTime;
    }

    long getClientTimeOnSend() {
        return clientTimeOnSend;
    }

    long getClientTimeOnReceipt() {
        return clientTimeOnReceipt;
    }
    private String targetAppVersion = null;
    private long serverTimeOnReceipt = 0;
    private long serverTimeOnSend = 0;
    private long rebootTime = 0;
    private long clientTimeOnSend = 0;
    private long clientTimeOnReceipt = 0;
    private InputStream dataPostInput = null;
    private OutputStream dataPostOutput = null;
    private HttpConnection dataPostConnection = null;
    private final static int DATA_POST_MINIMUM_SERVER_RESPONSE_SIZE = 3;
    private String appVersion = null;
    private HostManager hostManager = null;
    private final String alternateDataPostUri;
}
