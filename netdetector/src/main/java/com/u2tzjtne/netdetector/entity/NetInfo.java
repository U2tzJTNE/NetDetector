package com.u2tzjtne.netdetector.entity;

public class NetInfo {
    public final String name;
    public final NetType type;
    public final long handle;
    public final IPAddress[] ipAddresses;

    public NetInfo(String name, NetType type, long handle,
                   IPAddress[] addresses) {
        this.name = name;
        this.type = type;
        this.handle = handle;
        this.ipAddresses = addresses;
    }
}
