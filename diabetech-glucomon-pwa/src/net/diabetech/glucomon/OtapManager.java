package net.diabetech.glucomon;

import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2011 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class OtapManager {

    private OtapManager() {
    }

    static boolean updateApplication(
            final HostManager hostManager,
            final String targetAppVersion,
            final String appVer) {

        if (targetAppVersion == null || targetAppVersion.equals("0")) {
            Logger.log("No valid target app version to work with for otap");
            return false;
        }

        if (appVer != null && !appVer.equalsIgnoreCase(targetAppVersion)) {
            Logger.log("doOtap");
            String otapUri = hostManager.getOtapUri(targetAppVersion);
            String otapNotifyUri = hostManager.getDataPostUri();
            String apn = hostManager.getApn();
            Logger.log("OTAP", otapUri, otapNotifyUri, apn);
            return Tc65Module.otap(otapUri, otapNotifyUri, apn);
        }

        return false;
    }
}
