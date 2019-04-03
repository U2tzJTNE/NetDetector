package com.u2tzjtne.netdetector.detect;

import android.annotation.SuppressLint;
import android.net.ConnectivityManager;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.u2tzjtne.netdetector.proxy.ConnectivityProxy;
import com.u2tzjtne.netdetector.entity.NetInfo;
import com.u2tzjtne.netdetector.entity.NetType;

import static com.u2tzjtne.netdetector.detect.NetDetect.networkToNetId;

//android 5.0
@SuppressLint("NewApi")
public class NetCallback extends ConnectivityManager.NetworkCallback {
    private static final String TAG = "NetCallback";
    private Observer observer;
    private ConnectivityProxy connectivityProxy;

    public NetCallback(Observer observer, ConnectivityProxy connectivityProxy) {
        this.observer = observer;
        this.connectivityProxy = connectivityProxy;
    }

    @Override
    public void onAvailable(Network network) {
        Log.d(TAG, "Network becomes available: " + network.toString());
        onNetworkChanged(network);
    }

    @Override
    public void onCapabilitiesChanged(
            Network network, NetworkCapabilities networkCapabilities) {
        // A capabilities change may indicate the NetType has changed,
        // so forward the new NetInfo along to the observer.
        Log.d(TAG, "capabilities changed: " + networkCapabilities.toString());
        onNetworkChanged(network);
    }

    @Override
    public void onLinkPropertiesChanged(Network network, LinkProperties linkProperties) {
        // A link property change may indicate the IP address changes.
        // so forward the new NetInfo to the observer.
        Log.d(TAG, "link properties changed: " + linkProperties.toString());
        onNetworkChanged(network);
    }

    //网络即将断开
    @Override
    public void onLosing(Network network, int maxMsToLive) {
        // Tell the network is going to lose in MaxMsToLive milliseconds.
        // We may use this signal later.
        Log.d(TAG, "Network with handle " + networkToNetId(network) +
                " is about to lose in " + maxMsToLive + "ms");
    }

    //网络断开
    @Override
    public void onLost(Network network) {
        long handle = networkToNetId(network);
        Log.d(TAG, "Network with handle " + handle + " is disconnected");
        observer.onNetworkDisconnect(handle);
    }

    //网络发生变化
    private void onNetworkChanged(Network network) {
        NetInfo netInfo = connectivityProxy.networkToInfo(network);
        if (netInfo.type != NetType.TYPE_UNKNOWN
                && netInfo.type != NetType.TYPE_NONE) {
            observer.onNetworkConnect(netInfo);
        }
    }
}
