package net.diabetech.glucomon;

import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

class HostManagerImplSl01 implements HostManager {

    private final static String OTAP_DATA_POST_PROTOCOL = "http://";
    private final static String OTAP_DATA_POST_HOST = "mygluco.com";
    private final static String OTAP_DATA_POST_PORT = "80";
    private final static String OTAP_DIR = "otap";
    private final static String OTAP_JAD_FILE = "GlucoMonStationary.jad";
    private final static String OTAP_URI_BEGIN = OTAP_DATA_POST_PROTOCOL + OTAP_DATA_POST_HOST + ":" + OTAP_DATA_POST_PORT + "/" + OTAP_DIR + "/";
    private final static String OTAP_URI_END = "/" + OTAP_JAD_FILE;
    // nb: data since postback is to gateway!
    private final static String OTAP_NOTIFY_URI = "http://gdyx.diabetech.net/st/otap/notify/";
    // data
    private final static String DATA_POST_PROTOCOL = "http://";
    private final static String DATA_POST_HOST = "gdyx.diabetech.net";
    private final static String DATA_POST_PORT = "80";
    private final static String DATA_POST_FILE = "st/data/";
    private final static String DATA_POST_CONNECTION_PROFILE = "bearer_type=gprs;access_point=c1.korem2m.com;username=;password=";
    private final static String DATA_POST_CONNECTION_URI = DATA_POST_PROTOCOL + DATA_POST_HOST + ":" + DATA_POST_PORT + "/" + DATA_POST_FILE + ";" + DATA_POST_CONNECTION_PROFILE;

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
        return null;
    }

    public void registerOnPreferredNetwork() {
        String network = Tc65Module.registerOnNetwork(Tc65Module.NETWORK_ID_USA_CINGULAR);
        Logger.log(Tc65Module.NETWORK_ID_USA_CINGULAR, network);
        if (!Tc65Module.NETWORK_ID_USA_CINGULAR.equals(network)) {
            // fall back to auto if didn't register yet
            Tc65Module.registerOnNetworkAutomatically();
            network = Tc65Module.getOperatorId();
        }
        Logger.log("network", network);
    }

    public String getApn() {
        return "proxy";
    }
}
