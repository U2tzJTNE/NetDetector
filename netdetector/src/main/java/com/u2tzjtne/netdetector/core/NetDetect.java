package com.u2tzjtne.netdetector.core;


import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager.NetworkCallback;


public class NetDetect {

    private static final String TAG = "NetMonitorAutoDetect";
    //移动网络 回调
    private final NetworkCallback mobileNetworkCallback;
    //所有网络 回调
    private final NetworkCallback allNetworkCallback;
    // 测试
    private NetManger netManger;

    //主线程调用
    @SuppressLint("NewApi")
    public NetDetect(Context context) {
        // 网络类型更改的观察者
        netManger = new NetManger(context);

        //判断是否支持回调方式监听   即系统版本>5.0
        if (netManger.supportNetworkCallback()) {
            mobileNetworkCallback = new NetworkCallback();
            netManger.requestMobileNetwork(mobileNetworkCallback);
            allNetworkCallback = new NetCallback(netManger);
            netManger.registerNetworkCallback(allNetworkCallback);
        } else {
            mobileNetworkCallback = null;
            allNetworkCallback = null;
        }
    }

    //注册
    public void register(Object object) {

    }

    //销毁
    public void unRegister(Object object) {
        if (allNetworkCallback != null) {
            netManger.releaseCallback(allNetworkCallback);
        }
        if (mobileNetworkCallback != null) {
            netManger.releaseCallback(mobileNetworkCallback);
        }
    }
}