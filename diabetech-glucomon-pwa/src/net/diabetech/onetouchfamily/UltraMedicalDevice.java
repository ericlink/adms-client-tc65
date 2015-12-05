package net.diabetech.onetouchfamily;

import java.util.Calendar;
import net.diabetech.glucomon.Record;
import net.diabetech.glucomon.RecordSet;
import net.diabetech.lang.ArrayHelper;
import net.diabetech.util.CalendarHelper;
import net.diabetech.util.Logger;
import net.diabetech.util.StopWatch;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class UltraMedicalDevice extends AbstractOneTouchMedicalDevice {

    UltraUltra2LinkLayer uu2ll;

    UltraMedicalDevice(UltraUltra2LinkLayer uu2ll) {
        Logger.log("UltraMedicalDevice()");
        this.uu2ll = uu2ll;
    }

    public void setTime(Calendar currentTime) {
        Logger.log("UltraMedicalDevice.setTime()");
        Logger.log("currentTime", currentTime.getTime());
        StringBuffer command = new StringBuffer();
        command.append("DMT");
        command.append(CalendarHelper.getFormattedMonth(currentTime));
        command.append("/");
        command.append(CalendarHelper.getFormattedDayOfMonth(currentTime));
        command.append("/");
        command.append(CalendarHelper.getFormattedYear(currentTime));
        command.append(" ");
        command.append(CalendarHelper.getFormattedHourOfDay(currentTime));
        command.append(":");
        command.append(CalendarHelper.getFormattedMinute(currentTime));
        command.append("\r");
        Logger.log("command", command);
        uu2ll.sendCommandToMeter(command.toString().getBytes());
    }

    public RecordSet getRecordSet(RecordSet lastRecordSet) {
        Logger.log("UltraMedicalDevice.getRecordSet()");
        StopWatch sw = new StopWatch("getRecordSet");
        byte[] applicationResponse = uu2ll.sendCommandToMeter(COMMAND_DMP);
        Logger.log(sw.getElapsedTimeMessage());
        if (applicationResponse != null && applicationResponse.length > 0) {
            Record record = new Record(applicationResponse);
            byte[] uniqueId = ArrayHelper.copyBytes(applicationResponse, 0, HEADER_RECORD_LENGTH);
            if (uniqueId != null && uniqueId.length > 0) {
                RecordSet recordSet = new RecordSet(uniqueId, 1);
                recordSet.addRecord(record);
                return recordSet;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    private static final int HEADER_RECORD_LENGTH = 32;
    private static final byte[] COMMAND_DMP = new byte[]{(byte) 0x11, (byte) 0x0D, (byte) 'D', (byte) 'M', (byte) 'P'};
}
