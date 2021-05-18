package com.u2tzjtne.netdetector.sample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.u2tzjtne.netdetector.NetDetector;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetDetector.getDefault().register(this);
    }

    public void test1() {
        Log.d(TAG, "test1: 网络已连接");
    }

    public void test2() {
        Log.d(TAG, "test2: WIFI已连接");
    }

    public void test3() {
        Log.d(TAG, "test3: 移动网络已连接");
    }

    public void test4() {
        Log.d(TAG, "test4: 网络断开");
    }

    public void test5() {
        Log.d(TAG, "test4: 蓝牙网络");
    }

    public void test6() {
        Log.d(TAG, "test4: 以太网");
    }

    public void test7() {
        Log.d(TAG, "test4: 2G网络");
    }

    public void test8() {
        Log.d(TAG, "test4: 3G网络");
    }

    public void test9() {
        Log.d(TAG, "test4: 4G网络");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetDetector.getDefault().unRegister(this);
    }
}
