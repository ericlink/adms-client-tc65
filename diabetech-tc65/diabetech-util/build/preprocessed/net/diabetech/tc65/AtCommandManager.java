package net.diabetech.tc65;

import com.siemens.icm.io.*;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class AtCommandManager {

    private static ATCommand atCommand = null;

    private AtCommandManager() {
        Logger.log("AtCommandManager()");
    }

    /**
     * Synchronize use of AT command object to avoid problems with threads,
     * control timing and VM behavior around AT command use
     */
    static final synchronized String send(String cmd) {
        Logger.log("AtCommandManager.send()", cmd);
        try {
            if (atCommand == null) {
                atCommand = new ATCommand(false);
            }
            // siemens recommends send blocking 
            // siemens recommends avoid GPRS hangs
            System.gc();			    //siemens recommends
            String response = atCommand.send(cmd);  //siemens recommends
            Thread.sleep(100);                      //siemens recommends
            Logger.log("response", response);
            return response;                        //siemens recommends
        } catch (IllegalStateException e) {
            Logger.log(e);
        } catch (IllegalArgumentException e) {
            Logger.log(e);
        } catch (ATCommandFailedException e) {
            Logger.log(e);
        } catch (InterruptedException e) {
            Logger.log(e);
        }
        return "ERROR";
    }

    static final synchronized void release() {
        Logger.log("AtCommandManager.release()");
        try {
            if (atCommand != null) {
                try {
                    atCommand.breakConnection();
                } catch (Exception e) {
                    Logger.log(e);
                }
                atCommand.release();
            }
        } catch (Exception e) {
            Logger.log(e);
        } finally {
            atCommand = null;
        }

    }
}
