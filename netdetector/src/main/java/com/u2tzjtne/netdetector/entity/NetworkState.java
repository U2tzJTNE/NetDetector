package com.u2tzjtne.netdetector.entity;

public class NetworkState {
    private final boolean connected;
    //非移动设备
    private final int type;
    //移动设备
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
