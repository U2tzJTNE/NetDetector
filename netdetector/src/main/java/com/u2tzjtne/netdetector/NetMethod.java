package com.u2tzjtne.netdetector;

import com.u2tzjtne.netdetector.entity.NetType;

import java.lang.reflect.Method;

public class NetMethod {
    //网络类型
    private NetType netType;
    //需要执行的方法
    private Method method;

    public NetMethod(NetType netType, Method method) {
        this.netType = netType;
        this.method = method;
    }

    public NetType getNetType() {
        return netType;
    }

    public void setNetType(NetType netType) {
        this.netType = netType;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
