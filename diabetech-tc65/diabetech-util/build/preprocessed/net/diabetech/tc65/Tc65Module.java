package net.diabetech.tc65;

import net.diabetech.lang.StringHelper;

/**
 * Confidential Information.
 * Copyright (C) 2007-2009 Eric Link, All rights reserved.
 * PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 **/
public class Tc65Module {

    private Tc65Module() {
    }

    private static void calculateCachedModuleInfo() {
        calculateImei();
        calculateSimId();
    }

    private static void calculateImei() {
        String atResponse = AtCommandManager.send("\rAT+CGSN\r");
        imei = StringHelper.getDigits(atResponse);
    }

    private static void calculateSimId() {
        String atResponse = AtCommandManager.send("\rAT^SCID\r");
        simId = StringHelper.getDigits(atResponse);
    }

    private static String calculateOperatorId() {
        AtCommandManager.send("\rAT+COPS=3,2\r");
        String atResponse = AtCommandManager.send("\rAT+COPS?\r");
        return StringHelper.substringBetween(atResponse, "\"", "\"");
    }

    
    public final static String NETWORK_ID_USA_CINGULAR = "310410";
    public final static String NETWORK_ID_USA_TMOBILE_1 = "310026";
    public final static String NETWORK_ID_USA_TMOBILE_2 = "310260";
    public final static String NETWORK_ID_USA_TMOBILE_3 = "310490";
    public final static String NETWORK_ID_UK_O2 = "23410";
    public final static String NETWORK_ID_UK_VODAFONE = "23415";
    public final static String NETWORK_ID_UK_ORANGE = "23433";
    public final static String NETWORK_ID_UK_BT_1 = "23400";
    public final static String NETWORK_ID_UK_BT_2 = "23477";
    public final static String NETWORK_ID_UK_VIRGIN_1 = "23431";
    public final static String NETWORK_ID_UK_VIRGIN_2 = "23432";
    public final static String NETWORK_ID_UK_JTWAVE = "23450";

    /**
     * @param networkId to try to register on (NETWORK_ID_x)
     * @return network id that is currently registered, if any
     */
    public static String registerOnNetwork(final String networkId) {
        // manual registration mode
        // AT+COPS=2
        AtCommandManager.send("\rAT+COPS=2\r");
        // try to register with network
        // AT+COPS=1,2,"310410"
        String atResponse = AtCommandManager.send("\rAT+COPS=1,2,\"" + networkId + "\"\r");
        return calculateOperatorId();
    }

    public static boolean registerOnNetworkAutomatically() {
        // use automatic registration mode
        // AT+COPS=0
        String atResponse = AtCommandManager.send("\rAT+COPS=0\r");
        return atResponse != null && atResponse.indexOf("OK") > 0;
    }
    public static boolean setApn(String apn) {
        String atResponse = AtCommandManager.send("\rAT^SJNET=" + apn + "\r");
        return atResponse != null && atResponse.indexOf("OK") > 0;
    }
    private static String calculateSignalQuality() {
        String atResponse = AtCommandManager.send("\rAT+CSQ\r");
        return StringHelper.substringBetween(atResponse, " ", "\r");
    }

    public static String getImei() {
        if (imei == null) {
            calculateCachedModuleInfo();
        }
        return imei;
    }

    public static String getSimId() {
        if (simId == null) {
            calculateCachedModuleInfo();
        }
        return simId;
    }

    /**
     * Do not cache; may change over module uptme
     **/
    public static String getOperatorId() {
        return calculateOperatorId();
    }

    /**
     * Do not cache; may change over module uptime
     **/
    public static String getSignalQuality() {
        return calculateSignalQuality();
    }

    public static void restartModule() {
        AtCommandManager.send("\rAT+CFUN=0,1\r");
    }

    public static void shutdownModule() {
        AtCommandManager.send("\rAT^SMSO\r");
    }

    public static boolean otap(final String OTAP_URI, final String OTAP_NOTIFY_URI, final String apn) {
        AtCommandManager.send("\rAT^SJOTAP=\"Jd3jK9u\",\"" + OTAP_URI + "\",\"a:/ota\",\"Q4tFPi!rp6ZV)ozv\",\"m!HPre8WJB@hvfnJ\",\"gprs\",\"" + apn + "\",,,,\"" + OTAP_NOTIFY_URI + "\"\r");
        String response = AtCommandManager.send("\rAT^SJOTAP\r");
        return response != null && response.indexOf("OK") > 0;
    }

    /**
     * Release any resources held by the class
     **/
    public static void release() {
        AtCommandManager.release();
    }
    private static String imei;
    private static String simId;
}
