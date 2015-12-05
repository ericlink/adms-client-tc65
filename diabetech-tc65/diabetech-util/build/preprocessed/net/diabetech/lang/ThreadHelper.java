package net.diabetech.lang;

import net.diabetech.util.*;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class ThreadHelper {

    private ThreadHelper() {
    }

    public static void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException ie) {
            // ok, continue
            Logger.log("TimerManager.sleep() interrupted", milliseconds);
        }
    }
}
