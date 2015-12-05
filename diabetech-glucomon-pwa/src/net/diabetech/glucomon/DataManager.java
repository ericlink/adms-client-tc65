package net.diabetech.glucomon;

import java.util.Enumeration;
import java.util.Vector;
import net.diabetech.util.Logger;

/**
 * Manager record sets that are current
 * and previously transmitted and return data bytes to transmit to the serverr.
 * @author elink
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
class DataManager {

    DataManager() {
    }

    /**
     * Set the RecordSet that is currently being processed
     **/
    void setCurrentRecordSet(RecordSet currentRecordSet) {
        this.currentRecordSet = currentRecordSet;
    }

    /**
     * For the current RecordSet,
     * calculates the delta,
     * packages the payload,
     * and returns the data bytes that need to be transmitted.
     * @return bytes that need to be sent to the server
     **/
    byte[] getDataToTransmit() {
        recordsToTransmit = null;
        dataToTransmit = null;
        calculateDelta();
        packagePayload();
        return dataToTransmit;
    }

    /**
     * Notify the DataManager that the current record set
     * was sent successfully, so we can record the data
     * as sent and also clear out the current record set
     *
     * Successfully sent data is recorded here so it can be considered
     * when calculating the delta to send next time
     **/
    void notifyDataSentSuccessfully() {
        Logger.log("notifyDataSentSuccessfully()");
        Logger.log("recordsToTransmit", recordsToTransmit);
        previousRecordSetsTransmitted.addElement(recordsToTransmit);
        recordsToTransmit = null;
        dataToTransmit = null;
        if (previousRecordSetsTransmitted.size() > NUMBER_OF_PREVIOUS_DATA_SETS_TO_RETAIN) {
            Logger.log("Pruning record sets transmitted list");
            previousRecordSetsTransmitted.removeElementAt(0);
        }
    }

    /**
     * Get the cached record set for this medical device,
     * if there is one.  This will be used to optimize fetching
     * from the medical device if possible (in the medical device
     * implementation logic)
     * */
    RecordSet getPreviouslySentRecordSet(MedicalDevice medicalDevice) {
        return null;
    }

    private void calculateDelta() {
        if (currentRecordSet != null && isNewDataAvailableToSend()) {
            recordsToTransmit = currentRecordSet;
        }
    }

    /**
     * Has this payload been sent before?  Compare the payload byte arrays.
     * @param medicalDevice
     * @return
     */
    private boolean isNewDataAvailableToSend() {
        Logger.log("isNewDataAvailableToSend()");
        try {
            Enumeration sentItems = previousRecordSetsTransmitted.elements();
            while (sentItems.hasMoreElements()) {
                RecordSet sent = (RecordSet) sentItems.nextElement();
                Logger.log("sent", sent);
                Logger.log("currentRecordSet", currentRecordSet);
                if (sent.equals(currentRecordSet)) {
                    Logger.log("Found current record set in sent list");
                    return false;
                }
            }
            Logger.log("Current record set not found in sent list");
            return true;
        } catch (Throwable t) {
            return true;
        }

    }

    /**
     * Take the current record set format for transmission to the server.
     **/
    private void packagePayload() {
        if (recordsToTransmit != null) {
            dataToTransmit = recordsToTransmit.serializeToByteArray();
        }

    }
    private final static int NUMBER_OF_PREVIOUS_DATA_SETS_TO_RETAIN = 3;
    private Vector previousRecordSetsTransmitted = new Vector();
    private RecordSet currentRecordSet = null;
    private RecordSet recordsToTransmit = null;
    private byte[] dataToTransmit = null;
}
