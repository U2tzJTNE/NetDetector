package com.u2tzjtne.netdetector;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.support.annotation.RequiresApi;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class NetCallbackImpl extends ConnectivityManager.NetworkCallback {
    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        NetDetector.getDefault().post(NetType.ALL);
    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        NetDetector.getDefault().post(NetType.NONE);
    }

    @Override
    public void onCapabilitiesChanged(Network network, NetworkCapabilities networkCapabilities) {
        super.onCapabilitiesChanged(network, networkCapabilities);
        if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){
            if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                NetDetector.getDefault().post(NetType.WIFI);
            }else {
                NetDetector.getDefault().post(NetType.GPRS);
            }
        }
    }
}
