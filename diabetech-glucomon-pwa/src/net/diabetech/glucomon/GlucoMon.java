package net.diabetech.glucomon;

import java.io.IOException;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2011 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class GlucoMon {

    public GlucoMon(
            String appVersion,
            String medicalDeviceFactoryClassName,
            String host,
            boolean sendStartupMessage,
            boolean shutdownAfterDataMessage,
            boolean runDiagnostics)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        Logger.log("GlucoMon()", appVersion, medicalDeviceFactoryClassName, host);
        this.appVersion = appVersion;
        this.medicalDeviceFactoryClassName = medicalDeviceFactoryClassName;
        this.host = host;
        this.hostManager = (HostManager) Class.forName("net.diabetech.glucomon.HostManagerImpl" + host).newInstance();
        messageManager = new MessageManager(appVersion, hostManager);
        medicalDeviceFactory = (MedicalDeviceFactory) Class.forName(medicalDeviceFactoryClassName).newInstance();
        this.sendStartupMessage = sendStartupMessage;
        this.shutdownAfterOnePass = shutdownAfterDataMessage;
        this.runDiagnostics = runDiagnostics;
    }

    /**
     * Send the startup message after the module has had time to get initialized.
     **/
    public void start() {
        Logger.log("start()");
        try {
            hostManager.registerOnPreferredNetwork();
            sendStartupMessage();
            // make diagnostics a startup message reply option?
            runDiagnostics();
        } catch (Throwable t) {
            Logger.log("start() Throwable: ", t.toString(), t.getMessage());
        } finally {
            // run the application (even if there was an exception
            // sending the startup message)
            // module time will be set on next communication with server
            run();
        }
    }

    /**
     * Main program logic and exception management
     *
     * Time Setting notes:
     *      - before (1st thing) so set if we are synched already, before user undocks.
     *      - after, just in case weren't synched but now we are (but user will undock per light and this will be an io exception. startup message should synch tie already so very rare case).
     *      - so only set at beginning of run loop
     ***/
    private void run() {
        Logger.log("run()");
        while (true) {
            try {
                moduleManager.pauseBetweenMedicalDevicePolling();
                //v3 moduleManager.checkForReboot();
                medicalDevice = medicalDeviceFactory.create();
                if (medicalDevice != null) {
                    boolean isOtapInProgress = false;
                    try {
                        Logger.log("Medical device", medicalDevice.getClass().getName());
                        // set time here to make sure it's set
                        // (e.g. user has not undocked so will be set)
                        setMedicalDeviceTime();
                        getData();
                        if (sendData()) {
                            isOtapInProgress = processResponse();
                            // set time here in case we received a time update
                            if (!isOtapInProgress) {
                                setMedicalDeviceTime();
                            }
                        }
                    } finally {
                        // shut down after getting a medical device
                        if (shutdownAfterOnePass) {
                            if (!isOtapInProgress) {
                                Tc65Module.shutdownModule();
                                // try to send startup msg, regardless of settings?
                            }
                        }
                    }
                }
            } catch (IOException ioe) {
                Logger.log("run() IOException: ", ioe.toString());
                if ("java.io.IOException: Profile could not be activated".equals(ioe.toString())) {
                    // once profile can not be activated, a module restart is the
                    // only way to get things reset and re-establish communications
                    Tc65Module.restartModule();
                }
            } catch (Throwable t) {
                Logger.log("run() Throwable: ", t.toString());
            } finally {
                destroy();
            }
        }
    }

    /**
     * @return true if OTAP is done and returns OK
     * @throws NumberFormatException
     */
    private boolean processResponse() throws NumberFormatException {
        Logger.log("processMessageResponse()");
        moduleManager.setClock(
                messageManager.getServerTimeOnReceipt(),
                messageManager.getServerTimeOnSend(),
                messageManager.getClientTimeOnSend(),
                messageManager.getClientTimeOnReceipt());
        //v3 moduleManager.setRebootTime(messageManager.getRebootTime());

        return OtapManager.updateApplication(
                hostManager,
                messageManager.getTargetAppVersion(),
                appVersion);
    }

    private void getData() throws IOException {
        Logger.log("getData()");
        RecordSet previousRecordSet = dataManager.getPreviouslySentRecordSet(medicalDevice);
        RecordSet newRecordSet = medicalDevice.getRecordSet(previousRecordSet);
        dataManager.setCurrentRecordSet(newRecordSet);
    }

    private void runDiagnostics() throws IOException {
        Logger.log("runDiagnostics()");

        if (!runDiagnostics) {
            return;
        }

        final int NUMBER_OF_TIMES_TO_SEND = 1;
        for (int passes = 0; passes < NUMBER_OF_TIMES_TO_SEND; passes++) {
            try {
                // pause at start of loop, give otap on startup a chance
                final long FIVE_MINUTES = 1000 * 60 * 5L;
                ThreadHelper.sleep(FIVE_MINUTES);
                String[] diagnosticUris = hostManager.getDiagnosticUris();
                if (diagnosticUris != null) {
                    //26 + cr/lf bytes test basic short message w/o chunking
                    byte[] shortMessage = new byte[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '\n', '\r'};
                    // 4096 = test 2k chunking
                    byte[] longMessage = new byte[4096];
                    for (int mi = 0; mi < longMessage.length; mi++) {
                        longMessage[mi] = shortMessage[mi % shortMessage.length];
                    }
                    // 30533 = size of 500 readings ultra2 messsage
                    byte[] longestMessage = new byte[30533];
                    for (int mi = 0; mi < longestMessage.length; mi++) {
                        longestMessage[mi] = shortMessage[mi % shortMessage.length];
                    }
                    // send the test messages
                    for (int i = 0; i < diagnosticUris.length; i++) {
                        sendDiagnosticMessage(diagnosticUris[i], shortMessage);
                        sendDiagnosticMessage(diagnosticUris[i], longMessage);
                        sendDiagnosticMessage(diagnosticUris[i], longestMessage);
                    }
                }
            } catch (Throwable t) {
                Logger.log("Throwable in runDiagnostics() ", t.toString());
            } finally {
                sendStartupMessage();
            }
        }
    }

    private boolean sendData() throws IOException {
        Logger.log("sendData()");
        messageManager.reset();
        byte[] message = dataManager.getDataToTransmit();
        if (message != null) {
            messageManager.sendDataMessage(message);
            dataManager.notifyDataSentSuccessfully();
            Logger.log("data sent");
            return true;
        }
        Logger.log("no data sent");
        return false;
    }

    private void sendDiagnosticMessage(String diagnosticUrl, byte[] message) throws IOException {
        Logger.log("sendDiagnosticMessage() message.length=", message.length);
        MessageManager diagnosticMessageManager = new MessageManager(appVersion, hostManager, diagnosticUrl);
        diagnosticMessageManager.sendDataMessage(message);
        diagnosticMessageManager.destroy();
        diagnosticMessageManager = null;
        Tc65Module.release();
    }

    private void sendStartupMessage() throws NumberFormatException, IOException {
        if (sendStartupMessage) {
            moduleManager.pauseBeforeStartup();
            messageManager.sendStartupMessage(medicalDeviceFactoryClassName, sendStartupMessage, shutdownAfterOnePass);
            Tc65Module.release();
            processResponse();
        }
    }

    private void setMedicalDeviceTime() {
        Logger.log("setMedicalDeviceTime()");
        if (moduleManager.getClockManager() != null) {
            medicalDevice.setTime(moduleManager.getClockManager().getManagedCalendar());
        }
    }

    /**
     * close serial ports during probe.
     * destroy method on factory interface
     * close post resources for messages
     * notifying everyone allows resources to be held
     * wherever and however long by different implementations
     **/
    public void destroy() {
        Logger.log("destroy()");
        if (medicalDeviceFactory != null) {
            medicalDeviceFactory.destroy();
            //keep handle
        }
        if (medicalDevice != null) {
            medicalDevice.destroy();
            medicalDevice = null;
        }
        if (messageManager != null) {
            messageManager.destroy();
            //keep handle
        }
    }
    private final String medicalDeviceFactoryClassName;
    private MedicalDeviceFactory medicalDeviceFactory;
    private MedicalDevice medicalDevice;
    private MessageManager messageManager;
    private DataManager dataManager = new DataManager();
    private ModuleManager moduleManager = new ModuleManager();
    private HostManager hostManager;
    private final String appVersion;
    private final String host;
    private final boolean sendStartupMessage;
    private final boolean shutdownAfterOnePass;
    private final boolean runDiagnostics;
}
