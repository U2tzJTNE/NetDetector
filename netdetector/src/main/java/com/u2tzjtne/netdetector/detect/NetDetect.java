package com.u2tzjtne.netdetector.detect;


import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.Network;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.u2tzjtne.netdetector.proxy.ConnectivityProxy;
import com.u2tzjtne.netdetector.proxy.WifiProxy;
import com.u2tzjtne.netdetector.entity.NetInfo;
import com.u2tzjtne.netdetector.entity.NetType;
import com.u2tzjtne.netdetector.entity.NetworkState;

public class NetDetect extends BroadcastReceiver {

    private static final String TAG = "NetMonitorAutoDetect";
    // 网络类型更改的观察者
    private final Observer observer;
    private final IntentFilter intentFilter;
    private final Context context;
    //移动网络 回调
    private final NetworkCallback mobileNetworkCallback;
    //所有网络 回调
    private final NetworkCallback allNetworkCallback;
    // 测试
    private ConnectivityProxy connectivityProxy;
    private WifiProxy wifiProxy;

    //是否已经注册
    private boolean isRegistered;
    //连接类型
    private NetType netType;
    //wifi的SSID
    private String wifiSSID;

    //主线程调用
    @SuppressLint("NewApi")
    public NetDetect(Observer observer, Context context) {
        this.observer = observer;
        this.context = context;
        connectivityProxy = new ConnectivityProxy(context);
        wifiProxy = new WifiProxy(context);
        final NetworkState networkState = connectivityProxy.getNetworkState();
        netType = getConnectionType(networkState);
        wifiSSID = getWifiSSID(networkState);
        //广播过滤 针对7.0之前
        intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        //注册广播
        registerReceiver();

        //判断是否支持回调方式监听   即系统版本>5.0
        if (connectivityProxy.supportNetworkCallback()) {
            mobileNetworkCallback = new NetworkCallback();
            connectivityProxy.requestMobileNetwork(mobileNetworkCallback);
            allNetworkCallback = new NetCallback(observer, connectivityProxy);
            connectivityProxy.registerNetworkCallback(allNetworkCallback);
        } else {
            mobileNetworkCallback = null;
            allNetworkCallback = null;
        }
    }

    //获取当前所有的网络连接
    NetInfo[] getActiveNetworkList() {
        return connectivityProxy.getActiveNetworkList();
    }

    //销毁
    public void destroy() {
        if (allNetworkCallback != null) {
            connectivityProxy.releaseCallback(allNetworkCallback);
        }
        if (mobileNetworkCallback != null) {
            connectivityProxy.releaseCallback(mobileNetworkCallback);
        }
        unregisterReceiver();
    }

    //注册广播
    private void registerReceiver() {
        if (isRegistered) return;
        isRegistered = true;
        context.registerReceiver(this, intentFilter);
    }

    //注销广播
    private void unregisterReceiver() {
        if (!isRegistered) return;
        isRegistered = false;
        context.unregisterReceiver(this);
    }

    //获取当前网络信息
    public NetworkState getCurrentNetworkState() {
        return connectivityProxy.getNetworkState();
    }

    /**
     * 返回用于*通信的设备当前默认连接网络的NetID
     * 仅在Lollipop和更新版本上实现，
     * 未实现时返回INVALID_NET_ID
     * <p>
     * 5.0+
     */
    @SuppressLint("NewApi")
    public long getDefaultNetId() {
        return connectivityProxy.getDefaultNetId();
    }

    //根据网络信息 获取网络连接类型
    public static NetType getConnectionType(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return NetType.TYPE_NONE;
        }
        switch (networkState.getNetworkType()) {
            case ConnectivityManager.TYPE_ETHERNET:
                return NetType.TYPE_ETHERNET;
            case ConnectivityManager.TYPE_WIFI:
                return NetType.TYPE_WIFI;
            case ConnectivityManager.TYPE_WIMAX:
                return NetType.TYPE_4G;
            case ConnectivityManager.TYPE_BLUETOOTH:
                return NetType.TYPE_BLUETOOTH;
            case ConnectivityManager.TYPE_MOBILE:
                // Use information from TelephonyManager to classify the connection.
                switch (networkState.getNetworkSubType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetType.TYPE_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetType.TYPE_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetType.TYPE_4G;
                    default:
                        return NetType.TYPE_UNKNOWN;
                }
            default:
                return NetType.TYPE_UNKNOWN;
        }
    }

    //获取WIFI的SSID
    private String getWifiSSID(NetworkState networkState) {
        if (getConnectionType(networkState) != NetType.TYPE_WIFI) return "";
        return wifiProxy.getWifiSSID();
    }

    //收到广播
    @Override
    public void onReceive(Context context, Intent intent) {
        final NetworkState networkState = getCurrentNetworkState();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            connectionTypeChanged(networkState);
        }
    }

    //连接类型变化
    private void connectionTypeChanged(NetworkState networkState) {
        NetType newNetType = getConnectionType(networkState);
        String newWifiSSID = getWifiSSID(networkState);
        if (newNetType == netType && newWifiSSID.equals(wifiSSID)) return;
        netType = newNetType;
        wifiSSID = newWifiSSID;
        Log.d(TAG, "Network connectivity changed, type is: " + netType);
        observer.onConnectionTypeChanged(newNetType);
    }

    /**
     * 返回表示此网络的句柄，以与NDK API一起使用。
     */
    @SuppressLint("NewApi")
    public static long networkToNetId(Network network) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return network.getNetworkHandle();
        } else {
            return Integer.parseInt(network.toString());
        }
    }
}