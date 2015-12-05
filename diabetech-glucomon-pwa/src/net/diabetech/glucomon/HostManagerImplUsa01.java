package net.diabetech.glucomon;

import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

class HostManagerImplUsa01 implements HostManager {

    private final static String OTAP_DATA_POST_PROTOCOL = "http://";
    private final static String OTAP_DATA_POST_HOST = "67.18.182.74";
    private final static String OTAP_DATA_POST_PORT = "3999";
    private final static String OTAP_DIR = "otap";
    private final static String OTAP_JAD_FILE = "GlucoMonStationary.jad";
    // dynamic, based on findingAPN for host; why does OTAP not use APN?
    private final static String OTAP_URI_BEGIN = OTAP_DATA_POST_PROTOCOL + OTAP_DATA_POST_HOST + ":" + OTAP_DATA_POST_PORT + "/" + OTAP_DIR + "/";
    private final static String OTAP_URI_END = "/" + OTAP_JAD_FILE;
    // nb: data since postback is to gateway!
    private final static String OTAP_NOTIFY_URI = "http://data.gmon.usa01.diabetech.net:3080/st/otap/notify/";
    // data
    //private final static String DATA_POST_PROTOCOL = "https://";
    private final static String DATA_POST_PROTOCOL = "http://";
    //private final static String DATA_POST_HOST = "data.gmon.usa01.diabetech.net";
    private final static String DATA_POST_HOST = "67.18.182.74";
    //private final static String DATA_POST_PORT = "3181";
    private final static String DATA_POST_PORT = "3080";
    //private final static String DATA_POST_FILE = "st/data/";
    private final static String DATA_POST_FILE = "data/";
    //private final static String DATA_POST_CONNECTION_PROFILE = "bearer_type=gprs;access_point=c1.korem2m.com;username=;password=";
    // dynamic, based on findingAPN for host
    private final static String DATA_POST_CONNECTION_PROFILE = "bearer_type=gprs;access_point=telargo.t-mobile.com;username=;password=";
    // dynamic, based on findingAPN for host
    private final static String DATA_POST_CONNECTION_URI = DATA_POST_PROTOCOL + DATA_POST_HOST + ":" + DATA_POST_PORT + "/" + DATA_POST_FILE + ";" + DATA_POST_CONNECTION_PROFILE;
    // diagnostic URLs
    // std https proxy to app server
    //https://data.gmon.usa01.diabetech.net:3181/st/data/
    private final static String DIAGNOSTIC_DATA_POST_CONNECTION_URI_0 = "https://" + DATA_POST_HOST + ":" + "3181" + "/" + "st/data/" + ";" + DATA_POST_CONNECTION_PROFILE;
    // http proxy to app server
    //http://data.gmon.usa01.diabetech.net:3080/data/
    private final static String DIAGNOSTIC_DATA_POST_CONNECTION_URI_1 = "http://" + DATA_POST_HOST + ":" + "3080" + "/" + "data/" + ";" + DATA_POST_CONNECTION_PROFILE;
    //no proxy, direct apache http and logged
    //http://data.gmon.usa01.diabetech.net:3999/log/logpost.php
    private final static String DIAGNOSTIC_DATA_POST_CONNECTION_URI_2 = "http://" + DATA_POST_HOST + ":" + "3999" + "/" + "log/logpost.php" + ";" + DATA_POST_CONNECTION_PROFILE;

    public String getOtapUri(String targetAppVersion) {
        return OTAP_URI_BEGIN + targetAppVersion + OTAP_URI_END;
    }

    public String getOtapNotifyUri() {
        return OTAP_NOTIFY_URI;
    }

    public String getDataPostUri() {
        return DATA_POST_CONNECTION_URI;
    }

    public String[] getDiagnosticUris() {
        return new String[]{
                    //DIAGNOSTIC_DATA_POST_CONNECTION_URI_0,
                    DIAGNOSTIC_DATA_POST_CONNECTION_URI_1,
                    //DIAGNOSTIC_DATA_POST_CONNECTION_URI_2
                };
    }

    public void registerOnPreferredNetwork() {
        Tc65Module.registerOnNetworkAutomatically();
        String network = Tc65Module.getOperatorId();
        Logger.log("network", network);
    }

    public String getApn() {
        return "telargo.t-mobile.com";
    }
}
