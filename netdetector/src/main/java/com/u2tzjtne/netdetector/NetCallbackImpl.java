package com.u2tzjtne.netdetector;

import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;

//TODO 1. WiFi 数据同时连接的情况下 断开wifi 会调用数据
//TODO 2. WiFi 数据同时连接的情况下 断开数据 会调用断网
//TODO 3. 连接wifi  会调用两次wifi连接
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetCallbackImpl extends ConnectivityManager.NetworkCallback {

    //连接到一个新的可以使用的网络
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        NetDetector.getDefault().post(NetType.ALL);
    }

    //网络严重丢失或网络故障
    @Override
    public void onLost(Network network) {
        super.onLost(network);
        NetDetector.getDefault().post(NetType.NONE);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                NetDetector.getDefault().post(NetType.WIFI);
            } else {
                NetDetector.getDefault().post(NetType.GPRS);
            }
        }
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        super.onLinkPropertiesChanged(network, linkProperties);

    }
}
