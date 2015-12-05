package net.diabetech.onetouchfamily;

import java.util.Calendar;
import net.diabetech.glucomon.RecordSet;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class UltraMiniMedicalDevice extends AbstractOneTouchMedicalDevice {

    UltraMiniMedicalDevice() {
    }

    public void setTime(Calendar currentTime) {
    }

    public RecordSet getRecordSet(RecordSet lastRecordSet) {
        return null;//new RecordSet("Mini".getBytes(),500);
    }
}
