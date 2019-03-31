package com.u2tzjtne.netdetector.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.u2tzjtne.netdetector.NetDetector;
import com.u2tzjtne.netdetector.NetType;
import com.u2tzjtne.netdetector.annotation.Network;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetDetector.getDefault().register(this);
    }

    @Network(netType = NetType.ALL)
    public void test1() {
        Log.d(TAG, "test1: 网络已连接");
    }

    @Network(netType = NetType.WIFI)
    public void test2() {
        Log.d(TAG, "test2: WIFI已连接");
    }
    @Network(netType = NetType.GPRS)
    public void test3() {
        Log.d(TAG, "test3: 数据已连接");
    }
    @Network(netType = NetType.NONE)
    public void test4() {
        Log.d(TAG, "test4: 网络断开");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDetector.getDefault().unRegister(this);
    }
}
