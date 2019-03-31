package com.u2tzjtne.netdetector.sample;

import android.app.Application;

import com.u2tzjtne.netdetector.NetDetector;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        NetDetector.getDefault().init(this);
    }
}
