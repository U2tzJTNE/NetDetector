package com.u2tzjtne.netdetector;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkRequest;
import android.os.Build;

public class NetDetector {
    private static NetDetector INSTANCE;
    private Application application;

    private NetManger netManger;

    private NetDetector() {
        netManger = new NetManger();
    }

    public static NetDetector getDefault() {
        if (INSTANCE == null) {
            synchronized (NetDetector.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NetDetector();
                }
            }
        }
        return INSTANCE;
    }

    public void init(Application application) {
        if (application == null) return;
        this.application = application;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ConnectivityManager.NetworkCallback networkCallback = new NetCallbackImpl();
            NetworkRequest.Builder builder = new NetworkRequest.Builder();
            NetworkRequest request = builder.build();
            ConnectivityManager connectivityManager = (ConnectivityManager) getApplication()
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager != null) {
                connectivityManager.registerNetworkCallback(request, networkCallback);
            }
        }
    }

    public Application getApplication() {
        if (application == null) {
            throw new RuntimeException("未初始化");
        }
        return application;
    }

    //发送
    public void post(NetType netType) {
        netManger.post(netType);
    }

    //注册
    public void register(Object object) {
        netManger.register(object);
    }

    //注销
    public void unRegister(Object object) {
        netManger.unRegister(object);
    }

    //注销全部
    public void unRegisterAll() {
        netManger.unRegisterAll();
    }

}
