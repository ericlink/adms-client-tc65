package net.diabetech.glucomon;

import net.diabetech.lang.ArrayHelper;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class Record {

    private byte[] data;

    public Record(byte[] data) {
        this.data = data;
    }

    public byte[] toByteArray() {
        return data;
    }

    public int hashCode() {
        int length = data != null ? data.length : 0;
        return 37 * 12 * length;
    }

    /**
     * Deep equals compare on data byte array that just considers whatever application specific data is in the data array.
     **/
    public boolean equals(Object o) {
        if (!(o instanceof Record)) {
            return false;
        }

        Record r = (Record) o;

        return (r == this) ||
                ArrayHelper.equals(this.data, r.data);

    }
}
