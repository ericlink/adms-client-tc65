package net.diabetech.glucomonstationary;

import javax.microedition.midlet.*;
import net.diabetech.glucomon.GlucoMon;
import net.diabetech.util.Logger;

/**
 * Bootstrap MIDlet with legagy GlucoMonStationary name
 * so that it backward compatible with units configured in the field.
 * Load MIDlet properties and configure the GlucoMon instance.
 * Manage MIDlet lifecycle events
 * Confidential Information.
 * Copyright (C) 2007-2011 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class GlucoMonStationary extends MIDlet {

    GlucoMon glucoMon = null;

    public GlucoMonStationary() throws Throwable {
        try {
            Logger.log("GlucoMonStationary()");
            final boolean isDebug = getPropertyDebug();
            Logger.setDebug(isDebug);
            String appVersion = getPropertyVersion();
            String medicalDeviceFactory = getPropertyMedicalDeviceFactory();
            String host = getPropertyHost();
            boolean sendStartupMessage = getPropertySendStartupMessage();
            boolean shutdownAfterOnePass = getPropertyShutdownAfterOnePass();
            boolean runDiagnostics = getPropertyRunDiagnostics();
            glucoMon = new GlucoMon(appVersion, medicalDeviceFactory, host, sendStartupMessage, shutdownAfterOnePass, runDiagnostics);
        } catch (Throwable t) {
            Logger.log(t);
            throw t;
        }
    }

    public void startApp() throws MIDletStateChangeException {
        Logger.log("startApp()");
        try {
            glucoMon.start();
        } catch (Throwable t) {
            Logger.log("startApp() Throwable: ", t.toString());
        }
    }

    public void pauseApp() {
        try {
            Logger.log("pauseApp()");
            glucoMon.destroy();
        } catch (Throwable t) {
            Logger.log("pauseApp() Throwable: ", t.toString());
        }
    }

    public void destroyApp(boolean cond) {
        try {
            Logger.log("destroyApp()", cond);
            glucoMon.destroy();
        } catch (Throwable t) {
            Logger.log("destroyApp() Throwable: ", t.toString());
        } finally {
            notifyDestroyed();
        }
    }

    private boolean getPropertyDebug() {
        boolean isDebug = "true".equalsIgnoreCase(getAppProperty("isDebug"));
        Logger.log("isDebug", isDebug);
        return isDebug;
    }

    private String getPropertyMedicalDeviceFactory() {
        String medicalDeviceFactory = getAppProperty("medicalDeviceFactory");
        Logger.log("medicalDeviceFactory", medicalDeviceFactory);
        return medicalDeviceFactory;
    }

    private String getPropertyVersion() {
        String appVersion = getAppProperty("MIDlet-Version");
        Logger.log("appVersion", appVersion);
        return appVersion;
    }

    private String getPropertyHost() {
        String host = getAppProperty("host");
        Logger.log("host", host);
        return host;
    }

    private boolean getPropertySendStartupMessage() {
        String sendStartupMessage = getAppProperty("sendStartupMessage");
        Logger.log("sendStartupMessage", sendStartupMessage);
        return sendStartupMessage != null && sendStartupMessage.equalsIgnoreCase("true");
    }

    private boolean getPropertyShutdownAfterOnePass() {
        String shutdownAfterOnePass = getAppProperty("shutdownAfterOnePass");
        Logger.log("shutdownAfterOnePass", shutdownAfterOnePass);
        return shutdownAfterOnePass != null && shutdownAfterOnePass.equalsIgnoreCase("true");
    }

    private boolean getPropertyRunDiagnostics() {
        String runDiagnostics = getAppProperty("runDiagnostics");
        Logger.log("runDiagnostics", runDiagnostics);
        return runDiagnostics != null && runDiagnostics.equalsIgnoreCase("true");
    }
}
