package net.diabetech.glucomon;

import net.diabetech.lang.ArrayHelper;
import java.util.Vector;
import net.diabetech.util.Logger;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class RecordSet {

    final private byte[] uniqueId;
    final private int maxRecords;
    private Vector applicationDefinedRecords = new Vector();

    public RecordSet(
            final byte[] uniqueId,
            final int maxRecords) {
        if (uniqueId == null) {
            throw new IllegalArgumentException("RecordSet serial number must not be null");
        }
        if (uniqueId.length == 0) {
            throw new IllegalArgumentException("RecordSet serial number must not be zero length");
        }
        if (maxRecords == 0) {
            throw new IllegalArgumentException("Please specify maxRecords to match medical device memory");
        }

        this.uniqueId = uniqueId;
        this.maxRecords = maxRecords;
    }

    public byte[] getUniqueId() {
        return uniqueId;
    }

    public int getMaxRecords() {
        return maxRecords;
    }

    public Record[] getRecords() {
        Record[] recordArray = new Record[applicationDefinedRecords.size()];
        applicationDefinedRecords.copyInto(recordArray);
        return recordArray;
    }

    public void addRecord(Record r) {
        applicationDefinedRecords.addElement(r);
        while (applicationDefinedRecords.size() > getMaxRecords()) {
            applicationDefinedRecords.removeElementAt(0);
        }
    }

    /**
     * Prune rsNewRecordSet so it only contains records that aren't in this record set.
     *
     * @return null if can't get a valid delta and delta record set (which may just have serial number) if everything is valid
     **/
    public RecordSet getNewRecordsDelta(RecordSet rsNewRecordSet) {
        if (rsNewRecordSet == null) {
            return null; //must have a serial number to have a record set
        }

        if (rsNewRecordSet.getRecords().length == 0) {
            return rsNewRecordSet;
        }

        /**
         * really only valid to compare
         * same meter because if diff sn then all records are new;
         * so in a multi sn
         * compare all records would always come up
         **/
        if (!ArrayHelper.equals(this.getUniqueId(), rsNewRecordSet.getUniqueId())) {
            throw new RuntimeException("RecordSet serial numbers must match");
        }

        // shallow copy just move the handles from the new to the delta
        RecordSet delta = new RecordSet(
                rsNewRecordSet.getUniqueId(),
                rsNewRecordSet.getMaxRecords());

        for (int i = 0; i < rsNewRecordSet.getRecords().length; i++) {
            Record newRecord = rsNewRecordSet.getRecords()[i];
            if (!applicationDefinedRecords.contains(newRecord)) {
                delta.addRecord(newRecord);
            }
        }

        return delta;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[RecordSet uniqueId=");
        sb.append(new String(uniqueId));
        if (applicationDefinedRecords != null) {
            sb.append(",record count=");
            sb.append(applicationDefinedRecords.size());
        }
        sb.append("]");

        return sb.toString();
    }

    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + this.maxRecords;
        return hash;
    }

    /**
     * @param other
     * @return true if same unique id and deep compare on serialized byte array is equal
     */
    public boolean equals(Object other) {
        Logger.log("RecordSet.equals()");
        if (!(other instanceof RecordSet)) {
            Logger.log("!(other instanceof RecordSet)");
            return false;
        }
        if (this == other) {
            Logger.log("this == other");
            return true;
        }
        RecordSet otherRecordSet = (RecordSet) other;
        if (!ArrayHelper.equals(getUniqueId(), otherRecordSet.getUniqueId())) {
            Logger.log("getUniqueId() !=");
            return false;
        }

        if (ArrayHelper.equals(this.serializeToByteArray(),
                otherRecordSet.serializeToByteArray())) {
            Logger.log("serializeToByteArray() ==");
            return true;
        }

        Logger.log("return false");
        return false;
    }

    public byte[] serializeToByteArray() {
        if (applicationDefinedRecords.size() > 0) {
            Record record = (Record) applicationDefinedRecords.elementAt(0);
            return record.toByteArray();
        }

        return null;
    }
}
