package com.u2tzjtne.netdetector.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.u2tzjtne.netdetector.entity.IPAddress;
import com.u2tzjtne.netdetector.entity.NetInfo;
import com.u2tzjtne.netdetector.entity.NetType;
import com.u2tzjtne.netdetector.entity.NetworkState;

import static android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET;
import static android.net.NetworkCapabilities.TRANSPORT_CELLULAR;

public class NetManger {

    private static final int INVALID_NET_ID = -1;
    //在一些系统中connectivityManager 可能为null
    private final ConnectivityManager connectivityManager;

    public NetManger(Context context) {
        connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //返回当前默认网络信息
    public NetworkState getNetworkState() {
        if (connectivityManager == null) {
            return new NetworkState(false, -1, -1);
        }
        return getNetworkState(connectivityManager.getActiveNetworkInfo());
    }

    //返回network的连接类型 和 连接信息  Android 5.0+
    @SuppressLint("NewApi")
    public NetworkState getNetworkState(Network network) {
        if (connectivityManager == null) {
            return new NetworkState(false, -1, -1);
        }
        return getNetworkState(connectivityManager.getNetworkInfo(network));
    }

    //返回从networkInfo收集的信息
    public NetworkState getNetworkState(NetworkInfo networkInfo) {
        if (networkInfo == null || !networkInfo.isConnected()) {
            return new NetworkState(false, -1, -1);
        }
        return new NetworkState(true, networkInfo.getType(), networkInfo.getSubtype());
    }

    //5.0+
    @SuppressLint("NewApi")
    public Network[] getAllNetworks() {
        if (connectivityManager == null) {
            return new Network[0];
        }
        return connectivityManager.getAllNetworks();
    }

    public NetInfo[] getActiveNetworkList() {
        if (!supportNetworkCallback()) {
            return new NetInfo[0];
        }
        Network[] networks = getAllNetworks();
        NetInfo[] netInfos = new NetInfo[networks.length];
        for (int i = 0; i < networks.length; ++i) {
            netInfos[i] = networkToInfo(networks[i]);
        }
        return netInfos;
    }

    //返回当前默认网络的NetID  如果没有连接当前默认网络，则返回INVALID_NET_ID    5.0+
    @SuppressLint({"NewApi", "Assert"})
    public long getDefaultNetId() {
        if (!supportNetworkCallback()) {
            return INVALID_NET_ID;
        }
        /*
         *Android Lollipop没有API来获取默认网络;
         * 只有API才能返回默认网络的NetworkInfo。
         * 要确定默认网络，可以找到与默认网络类型匹配的网络
         * */
        final NetworkInfo defaultNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (defaultNetworkInfo == null) {
            return INVALID_NET_ID;
        }
        final Network[] networks = getAllNetworks();
        long defaultNetId = INVALID_NET_ID;
        for (Network network : networks) {
            if (!hasInternetCapability(network)) {
                continue;
            }
            final NetworkInfo networkInfo = connectivityManager.getNetworkInfo(network);
            if (networkInfo != null && networkInfo.getType() == defaultNetworkInfo.getType()) {
                /*
                 * 不应该有多个相同类型的连接网络
                 * 至少从Android Marshmallow开始，
                 * 这是不受支持的。如果支持，
                 * 则可以触发此断言。此时我们可以考虑使用
                 * ConnectivityManager.getDefaultNetwork（）
                 * 虽然这可能会给VPN带来令人困惑的结果，
                 * 并且只有可用于Android Marshmallow。
                 * */
                assert defaultNetId == INVALID_NET_ID;
                defaultNetId = networkToNetId(network);
            }
        }
        return defaultNetId;
    }

    /**
     * 返回表示此网络的句柄，以与NDK API一起使用。
     */
    @SuppressLint("NewApi")
    public long networkToNetId(Network network) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return network.getNetworkHandle();
        } else {
            return Integer.parseInt(network.toString());
        }
    }

    @SuppressLint("NewApi")
    public NetInfo networkToInfo(Network network) {
        LinkProperties linkProperties = connectivityManager.getLinkProperties(network);
        return new NetInfo(
                linkProperties.getInterfaceName(),
                getConnectionType(getNetworkState(network)),
                networkToNetId(network),
                getIPAddresses(linkProperties));
    }

    //根据网络信息 获取网络连接类型
    public NetType getConnectionType(NetworkState networkState) {
        if (!networkState.isConnected()) {
            return NetType.NET_NONE;
        }
        switch (networkState.getNetworkType()) {
            case ConnectivityManager.TYPE_ETHERNET:
                return NetType.NET_ETHERNET;
            case ConnectivityManager.TYPE_WIFI:
                return NetType.NET_WIFI;
            case ConnectivityManager.TYPE_WIMAX:
                return NetType.NET_4G;
            case ConnectivityManager.TYPE_BLUETOOTH:
                return NetType.NET_BLUETOOTH;
            case ConnectivityManager.TYPE_MOBILE:
                // 使用TelephonyManager中的信息对连接进行分类
                switch (networkState.getNetworkSubType()) {
                    case TelephonyManager.NETWORK_TYPE_GPRS:
                    case TelephonyManager.NETWORK_TYPE_EDGE:
                    case TelephonyManager.NETWORK_TYPE_CDMA:
                    case TelephonyManager.NETWORK_TYPE_1xRTT:
                    case TelephonyManager.NETWORK_TYPE_IDEN:
                        return NetType.NET_2G;
                    case TelephonyManager.NETWORK_TYPE_UMTS:
                    case TelephonyManager.NETWORK_TYPE_EVDO_0:
                    case TelephonyManager.NETWORK_TYPE_EVDO_A:
                    case TelephonyManager.NETWORK_TYPE_HSDPA:
                    case TelephonyManager.NETWORK_TYPE_HSUPA:
                    case TelephonyManager.NETWORK_TYPE_HSPA:
                    case TelephonyManager.NETWORK_TYPE_EVDO_B:
                    case TelephonyManager.NETWORK_TYPE_EHRPD:
                    case TelephonyManager.NETWORK_TYPE_HSPAP:
                        return NetType.NET_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NetType.NET_4G;
                    default:
                        return NetType.NET_UNKNOWN;
                }
            default:
                return NetType.NET_UNKNOWN;
        }
    }

    //如果可以提供Internet访问权限，则返回true 可用于忽略专用网络（例如IMS，FOTA） 5.0+
    @SuppressLint("NewApi")
    private boolean hasInternetCapability(Network network) {
        if (connectivityManager == null) {
            return false;
        }
        final NetworkCapabilities capabilities =
                connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NET_CAPABILITY_INTERNET);
    }

    //5.0+
    @SuppressLint("NewApi")
    public void registerNetworkCallback(ConnectivityManager.NetworkCallback networkCallback) {
        connectivityManager.registerNetworkCallback(
                new NetworkRequest.Builder().addCapability(NET_CAPABILITY_INTERNET).build(),
                networkCallback);
    }

    //5.0+
    @SuppressLint("NewApi")
    public void requestMobileNetwork(ConnectivityManager.NetworkCallback networkCallback) {
        NetworkRequest.Builder builder = new NetworkRequest.Builder();
        builder.addCapability(NET_CAPABILITY_INTERNET).addTransportType(TRANSPORT_CELLULAR);
        connectivityManager.requestNetwork(builder.build(), networkCallback);
    }

    //5.0+
    @SuppressLint("NewApi")
    public IPAddress[] getIPAddresses(LinkProperties linkProperties) {
        IPAddress[] ipAddresses = new IPAddress[linkProperties.getLinkAddresses().size()];
        int i = 0;
        for (LinkAddress linkAddress : linkProperties.getLinkAddresses()) {
            ipAddresses[i] = new IPAddress(linkAddress.getAddress().getAddress());
            ++i;
        }
        return ipAddresses;
    }

    //5.0+
    @SuppressLint("NewApi")
    public void releaseCallback(ConnectivityManager.NetworkCallback networkCallback) {
        if (supportNetworkCallback()) {
            connectivityManager.unregisterNetworkCallback(networkCallback);
        }
    }

    public boolean supportNetworkCallback() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && connectivityManager != null;
    }


    public void onNetworkConnect(Network network) {

    }

    public void onNetworkChanged(Network network) {

    }

    public void onNetworkDisconnect(Network network) {
        NetInfo netInfo = networkToInfo(network);
        if (netInfo.type != NetType.NET_UNKNOWN
                && netInfo.type != NetType.NET_NONE) {
        }
    }
}
