package net.diabetech.test;

import java.util.Calendar;
import net.diabetech.glucomon.MedicalDevice;
import net.diabetech.glucomon.RecordSet;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class MockMedicalDevice implements MedicalDevice {

    public MockMedicalDevice() {
    }

    public void setTime(Calendar time) {
    }

    public RecordSet getRecordSet(RecordSet lastRecordSet) {
        RecordSet rs = new RecordSet("UNIQUEID".getBytes(), 500);


//        byte[] header = "created:typeid:medicalDeviceTypeId:serialNumber:unitOfMeasure".getBytes();
//        rs.setApplicationDefinedHeader( header );
//
//        rs.addRecord( new Record( "data 01".getBytes() ) );
//        rs.addRecord( new Record( "data 02".getBytes() ) );
//        rs.addRecord( new Record( "data 03".getBytes() ) );
//        rs.addRecord( new Record( "data 04".getBytes() ) );
//        rs.addRecord( new Record( "data 05".getBytes() ) );

        return rs;
    }

    public void destroy() {
    }
}
