package net.diabetech.util;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * Manage clock with ntp algorithm.
 *
 * From WikiPedia
 * Basic Operation
 *
 * The basic operation of NTP that people may be familiar with is synchronising the 
 * clock in one computer to that in another. This works as follows:
 *
 * 1.   One computer (the client) sends a packet to a specified NTP Server. 
 *      In this packet it stores the time the packet left as defined by its clock (T1C)
 *
 * 2.   The NTP server receives the packet and notes the time it received the packet, 
 *      according to its clock (T1S)
 *
 * 3.   The NTP server sends a packet back to the client noting what time it
 *      was sent according to its clock (T2S). The packet sent back contains all 
 *      three time stamps, T1C, T1S and T2S
 *
 * 4.   The client receives the packet from the server and notes what time 
 *      it receives the packet according to its clock (T2C)
 *
 * The client now has four time stamps. 
 * From this it can determine the round-trip delay and clock offset.
 *
 * Let a = T1S - T1C
 * and b = T2S - T2C
 *
 * Round-trip delay, D = a - b and
 * Clock offset, O = (a+b)/2
 *
 **/
public class ClockManager {

    public ClockManager(
            long clientTimeOnSend,
            long clientTimeOnReceipt,
            long serverTimeOnReceipt,
            long serverTimeOnSend) {
        long clientToServerDelayAndOffset = (serverTimeOnReceipt - clientTimeOnSend);
        long serverToClientDelayAndOffset = (serverTimeOnSend - clientTimeOnReceipt);
        long roundTripDelay =
                clientToServerDelayAndOffset - serverToClientDelayAndOffset;
        systemTimeOffset =
                (clientToServerDelayAndOffset + serverToClientDelayAndOffset) / 2;
    }

    /**
     * @return current system time adjusted by date time synched from server 
     * (which will return date in whatever timezone the server used for it's calculations,
     *  usually the time zone based on the unit or user)
     **/
    public Date getManagedDate() {
        return new Date(getManagedMillis());
    }

    public long getManagedMillis() {
        return System.currentTimeMillis() + systemTimeOffset;
    }

    public Calendar getManagedCalendar() {
        TimeZone tz = TimeZone.getTimeZone("UTC");
        Calendar cal = Calendar.getInstance(tz);
        cal.setTime(getManagedDate());
        return cal;
    }
    private long systemTimeOffset;
}
