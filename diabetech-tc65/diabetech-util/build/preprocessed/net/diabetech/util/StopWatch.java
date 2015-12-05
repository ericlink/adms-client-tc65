package net.diabetech.util;

/**   
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class StopWatch {

    public StopWatch() {
        name = "StopWatch";
        reStart();
    }

    public StopWatch(String name) {
        this.name = name;
        reStart();
    }

    public void reStart() {
        start = System.currentTimeMillis();
    }

    public void stop() {
        end = System.currentTimeMillis();
    }

    public long getElapsedTimeMillis() {
        return end > 0 ? end - start : System.currentTimeMillis() - start;
    }

    public String getElapsedTimeMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append(name);
        sb.append(" elapsed time ");
        sb.append(getElapsedTimeMillis());
        sb.append("ms");
        return sb.toString();
    }
    private long start;
    private long end;
    private String name;
}
