package com.u2tzjtne.netdetector.core;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;

//android 5.0+
@SuppressLint("NewApi")
public class NetCallback extends ConnectivityManager.NetworkCallback {
    private NetManger netManger;

    NetCallback(NetManger netManger) {
        this.netManger = netManger;
    }

    @Override
    public void onAvailable(Network network) {
        netManger.onNetworkConnect(network);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        //功能更改可能表示NetType已更改
        netManger.onNetworkChanged(network);
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        //链接属性更改可能表示IP地址更改
        netManger.onNetworkChanged(network);
    }

    //网络断开
    @Override
    public void onLost(Network network) {
        netManger.onNetworkDisconnect(network);
    }

}
