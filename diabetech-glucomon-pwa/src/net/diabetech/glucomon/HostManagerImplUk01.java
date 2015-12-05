package net.diabetech.glucomon;

import java.io.IOException;
import javax.microedition.io.Connection;
import javax.microedition.io.Connector;
import javax.microedition.io.SocketConnection;
import net.diabetech.lang.StringHelper;
import net.diabetech.lang.ThreadHelper;
import net.diabetech.microedition.io.ConnectionHelper;
import net.diabetech.tc65.Tc65Module;
import net.diabetech.util.Logger;

class HostManagerImplUk01 implements HostManager {

    private static final String APN_CONNECTION_TEST_URI = "socket://67.18.182.74:8999";
    private final static String OTAP_DATA_POST_PROTOCOL = "http://";
    private final static String OTAP_DATA_POST_HOST = "67.18.182.74";
    private final static String OTAP_DATA_POST_PORT = "8999";
    private final static String OTAP_DIR = "otap";
    private final static String OTAP_JAD_FILE = "GlucoMonStationary.jad";
    private final static String OTAP_URI_BEGIN = OTAP_DATA_POST_PROTOCOL + OTAP_DATA_POST_HOST + ":" + OTAP_DATA_POST_PORT + "/" + OTAP_DIR + "/";
    private final static String OTAP_URI_END = "/" + OTAP_JAD_FILE;
    // nb: data since postback is to gateway!
    //private final static String OTAP_NOTIFY_URI = "http://data.gmon.g4h.diabetech.net:8080/st/otap/notify/";
    private final static String OTAP_NOTIFY_URI = "http://67.18.182.74:8080/st/otap/notify/";
    // data
    //private final static String DATA_POST_PROTOCOL = "https://";
    private final static String DATA_POST_PROTOCOL = "http://";
    //private final static String DATA_POST_HOST = "data.gmon.g4h.diabetech.net";
    private final static String DATA_POST_HOST = "67.18.182.74";
    //private final static String DATA_POST_PORT = "8181";
    private final static String DATA_POST_PORT = "8080";
    //private final static String DATA_POST_FILE = "st/data/";
    private final static String DATA_POST_FILE = "data/";
    private final static String DATA_POST_CONNECTION_URI = DATA_POST_PROTOCOL + DATA_POST_HOST + ":" + DATA_POST_PORT + "/" + DATA_POST_FILE;
    // APN
    private final static String DATA_POST_CONNECTION_PROFILE_KORE = "bearer_type=gprs;access_point=c1.korem2m.com;username=;password=";
    private final static String DATA_POST_CONNECTION_DIAG_URI = "https://" + "data.gmon.g4h.diabetech.net" + ":" + "8181" + "/" + "st/data/";
    private String dataPostDiagUri = null;
    private String dataPostUri = null;
    private String apn = "proxy";

    public String getOtapUri(String targetAppVersion) {
        return OTAP_URI_BEGIN + targetAppVersion + OTAP_URI_END;
    }

    public String getOtapNotifyUri() {
        return OTAP_NOTIFY_URI;
    }

    public String getDataPostUri() {
        return dataPostUri;
    }

    public String[] getDiagnosticUris() {
        return new String[]{
                    dataPostUri,
                    dataPostDiagUri
                };
    }
    private static String[] PREFERRED_NETWORKS = {
        Tc65Module.NETWORK_ID_UK_O2,
        Tc65Module.NETWORK_ID_UK_ORANGE,
        Tc65Module.NETWORK_ID_UK_VODAFONE,
        Tc65Module.NETWORK_ID_UK_BT_1,
        Tc65Module.NETWORK_ID_UK_BT_2,
        Tc65Module.NETWORK_ID_UK_VIRGIN_1,
        Tc65Module.NETWORK_ID_UK_VIRGIN_2,
        Tc65Module.NETWORK_ID_UK_JTWAVE,};

    public void registerOnPreferredNetwork() {
        //http://en.wikipedia.org/wiki/Mobile_Network_Code
        // try to register using preferred order
        String network = null;
        for (int i = 0; i < PREFERRED_NETWORKS.length; i++) {
            network = Tc65Module.registerOnNetwork(PREFERRED_NETWORKS[i]);
            Logger.log(PREFERRED_NETWORKS[i], network);
            if (PREFERRED_NETWORKS[i].equals(network)) {
                break;
            }
        }

        // not found?
        if (network == null || network.length() < 5) {
            Tc65Module.registerOnNetworkAutomatically();
            network = Tc65Module.getOperatorId();
        }

        Logger.log("network", network);

        if (network != null && network.length() > 4) {
            findApn();
        }

    }

    private void findApn() {
        // recommended to wait 30 seconds for GPRS to establish
        ThreadHelper.sleep(1000 * 30);

        for (int i = 0; i <
                apns.length; i++) {
            Connection conn = null;
            try {
                //findPreferredApn
                String atApn = "\"gprs\",\"" + apns[i] + "\",\"" + user[i] + "\",\"" + pass[i] + "\",\"" + gateway[i] + "\",0";
                Logger.log("atApn", atApn);
                Tc65Module.setApn(atApn);
                conn = (Connection) Connector.open(APN_CONNECTION_TEST_URI);
                ConnectionHelper.close(conn);
                this.apn = apns[i];
                dataPostUri =
                        DATA_POST_CONNECTION_URI + ";" + "bearer_type=gprs;access_point=" + apns[i] + ";username=" + user[i] + ";password=" + pass[i];
                dataPostDiagUri =
                        DATA_POST_CONNECTION_DIAG_URI + ";" + "bearer_type=gprs;access_point=" + apns[i] + ";username=" + user[i] + ";password=" + pass[i];
                Logger.log("dataPostUri", dataPostUri);
                break; // found it!

            } catch (IOException ex) {
                Logger.log(ex);
                ex.printStackTrace();
            } finally {
                ConnectionHelper.close(conn);
            }

        }

    }

    public String getApn() {
        return apn;
    }
    private static String[] apns = {
        "gprs.mywasp.ws", //Wireless Logic O2
        "wirelesslogic-hsdpa.co.uk", //Wireless Logic Vod
        "c1.korem2m.com", //KORE
        "payandgo.o2.co.uk", //O2 UK (pre-pay)
        "wap.o2.co.uk", //O2 UK (contract)
        "talkmobile.co.uk", //Talkmobile Pay Monthly Customers
        "payg.talkmobile.co.uk", //Talkmobile Pay and Go Customers
        "orangeinternet", // Orange UK
        "general.t-mobile.uk", // T-Mobile
        "btmobile.bt.com", //Operator: - BT Mobile
        "goto.virginmobile.uk", //Virgin Mobile
        "prepay.tesco-mobile.com", // Tesco Mobile
        "pepper", //Jersey Telecom
        "wap.vodafone.co.uk", //Vodafone (Contract)
        "pp.vodafone.co.uk", // Vodafone (pre-pay)
    };
    private static String[] user = {
        "health", //Wireless Logic O2
        "health", //Wireless Logic Vod
        "", //KORE
        "payandgo", //O2 UK (pre-pay)
        "o2wap", //O2 UK (contract)
        "wap", //Talkmobile Pay Monthly Customers
        "wap", //Talkmobile Pay and Go Customers
        "", // Orange UK
        "user", // T-Mobile
        "bt", //Operator: - BT Mobile
        "user", //Virgin Mobile
        "tescowap", // Tesco Mobile
        "", //Jersey Telecom
        "wap", //Vodafone (Contract)
        "wap", // Vodafone (pre-pay)
    };
    private static String[] pass = {
        "health", //Wireless Logic O2
        "health", //Wireless Logic Vod
        "", //KORE
        "password", //O2 UK (pre-pay)
        "password", //O2 UK (contract)
        "wap", //Talkmobile Pay Monthly Customers
        "wap", //Talkmobile Pay and Go Customers
        "", // Orange UK
        "wap", // T-Mobile
        "bt", //Operator: - BT Mobile
        "", //Virgin Mobile
        "password", // Tesco Mobile
        "", //Jersey Telecom
        "wap", //Vodafone (Contract)
        "wap", // Vodafone (pre-pay)
    };
    private static String[] gateway = {
        "", //Wireless Logic O2
        "", //Wireless Logic Vod
        "", //KORE
        "",//"193.113.200.195", //O2 UK (pre-pay)
        "",//"193.113.200.195", //O2 UK (contract)
        "", //212.183.137.012 Talkmobile Pay Monthly Customers
        "", //212.183.137.012 Talkmobile Pay and Go Customers
        "",//"192.168.71.35", // Orange UK
        "",//"149.254.1.10", // T-Mobile
        "",//"212.183.137.12", //Operator: - BT Mobile
        "",//"193.30.166.3", //Virgin Mobile
        "",//"193.113.200.195", // Tesco Mobile
        "",//"212.9.0.135", //Jersey Telecom
        "",//"212.183.137.12", //Vodafone (Contract)
        "",//"212.183.137.12", // Vodafone (pre-pay)
    };
}
