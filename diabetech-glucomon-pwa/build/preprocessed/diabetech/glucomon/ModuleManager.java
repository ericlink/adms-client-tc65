package net.diabetech.glucomon;

import net.diabetech.lang.ThreadHelper;
import net.diabetech.util.ClockManager;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class ModuleManager {

    ModuleManager() {
    }

    void pauseBeforeStartup() {
        ThreadHelper.sleep(NUMBER_OF_SECONDS_TO_WAIT_BEFORE_APP_STARTUP);
    }

    void pauseBetweenMedicalDevicePolling() {
        ThreadHelper.sleep(NUMBER_OF_SECONDS_TO_WAIT_BETWEEN_DATA_POLL_LOOPS);
    }
// v3
//    /**
//     * If there is a current module time and a valid reboot time,
//     * and the reboot time has passed, perform the reboot.
//     **/
//    void checkForReboot() {
//        if (       clockManager != null
//                && rebootTime > 0
//                && rebootTime < clockManager.getManagedMillis() ) {
//            Tc65Module.restartModule();
//        }
//    }

    ClockManager getClockManager() {
        return clockManager;
    }

    void setClock(
            final long serverTimeOnReceipt,
            final long serverTimeOnSend,
            final long clientTimeOnSend,
            final long clientTimeOnReceipt) {

        Logger.log("clientTimeOnSend", clientTimeOnSend);
        Logger.log("serverTimeOnReceipt", serverTimeOnReceipt);
        Logger.log("serverTimeOnSend", serverTimeOnSend);
        Logger.log("clientTimeOnReceipt", clientTimeOnReceipt);

        if (serverTimeOnReceipt == 0 || serverTimeOnSend == 0) {
            Logger.log("Can't set time, no info");
            return;
        }

        clockManager = new ClockManager(clientTimeOnSend, clientTimeOnReceipt,
                serverTimeOnReceipt, serverTimeOnSend);
    }
//v3
//    void setRebootTime(long rebootTime) {
//        this.rebootTime = rebootTime;
//    }
    private final static long NUMBER_OF_SECONDS_TO_WAIT_BEFORE_APP_STARTUP = 1000 * 5;
    private final static long NUMBER_OF_SECONDS_TO_WAIT_BETWEEN_DATA_POLL_LOOPS = 1000 * 5;
    private ClockManager clockManager = null;
    private long rebootTime;
}
