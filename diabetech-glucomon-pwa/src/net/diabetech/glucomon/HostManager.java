package net.diabetech.glucomon;

public interface HostManager {

    String getDataPostUri();

    String getOtapNotifyUri();

    String getOtapUri(String targetAppVersion);

    String[] getDiagnosticUris();
    
    void registerOnPreferredNetwork();

    String getApn();
}
