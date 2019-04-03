package com.u2tzjtne.netdetector.proxy;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
/**
 * Queries the WifiManager for SSID of the current Wifi connection.
 */
public class WifiProxy {
    private final Context context;

    public WifiProxy(Context context) {
        this.context = context;
    }

    public String getWifiSSID() {
        final Intent intent = context.registerReceiver(null,
                new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
        if (intent != null) {
            final WifiInfo wifiInfo = intent.getParcelableExtra(WifiManager.EXTRA_WIFI_INFO);
            if (wifiInfo != null) {
                final String ssid = wifiInfo.getSSID();
                if (ssid != null) {
                    return ssid;
                }
            }
        }
        return "";
    }
}