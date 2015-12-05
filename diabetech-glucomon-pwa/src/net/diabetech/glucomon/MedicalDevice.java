package net.diabetech.glucomon;

import java.util.Calendar;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public interface MedicalDevice {

    public void setTime(Calendar time);

    /**
     * @param lastRecordSet may be used by implementation to determine what data to return
     * @return
     */
    public RecordSet getRecordSet(RecordSet lastRecordSet);

    public void destroy();
}
