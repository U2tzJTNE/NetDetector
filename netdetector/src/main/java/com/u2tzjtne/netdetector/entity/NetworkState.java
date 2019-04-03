package com.u2tzjtne.netdetector.entity;

public class NetworkState {
    private final boolean connected;
    // Defined from ConnectivityManager.TYPE_XXX for non-mobile; for mobile, it is
    // further divided into 2G, 3G, or 4G from the subtype.
    private final int type;
    // Defined from NetworkInfo.subtype, which is one of the TelephonyManager.NETWORK_TYPE_XXXs.
    // Will be useful to find the maximum bandwidth.
    private final int subtype;

    public NetworkState(boolean connected, int type, int subtype) {
        this.connected = connected;
        this.type = type;
        this.subtype = subtype;
    }

    public boolean isConnected() {
        return connected;
    }

    public int getNetworkType() {
        return type;
    }

    public int getNetworkSubType() {
        return subtype;
    }
}
