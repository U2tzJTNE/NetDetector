package com.u2tzjtne.netdetector;

import com.u2tzjtne.netdetector.entity.NetType;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NetManger {
    private Map<Object, List<NetMethod>> netMap;

    public NetManger() {
        netMap = new HashMap<>();
    }

    //发送
    public void post(NetType netType) {
        Set<Object> objects = netMap.keySet();
        for (Object object : objects) {
            List<NetMethod> methods = netMap.get(object);
            if (methods != null) {
                for (NetMethod method : methods) {
                    if (method.getNetType() == netType) {
                        invoke(method, object);
                    }
                }
            }
        }
    }

    private void invoke(NetMethod method, Object object) {
        Method executor = method.getMethod();
        try {
            executor.invoke(object);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //注册
    public void register(Object object) {
        List<NetMethod> methods = netMap.get(object);
        if (methods == null) {
            methods = findAnnotationMethod(object);
            netMap.put(object, methods);
        }
    }

    private List<NetMethod> findAnnotationMethod(Object object) {
        List<NetMethod> list = new ArrayList<>();
        Class<?> clazz = object.getClass();
        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Network network = method.getAnnotation(Network.class);
            if (network == null) {
                continue;
            }
//            Type returnType = method.getGenericReturnType();
//            if (!"void".equalsIgnoreCase(returnType.toString())) {
//                throw new RuntimeException(method.getName() + "返回值不为空");
//            }
//            Class<?>[] parameterTypes = method.getParameterTypes();
//            if (parameterTypes.length > 0) {
//                throw new RuntimeException(method.getName() + "参数列表大于0");
//            }
            list.add(new NetMethod(network.netType(), method));
        }
        return list;
    }

    //注销
    public void unRegister(Object object) {
        if (!netMap.isEmpty()) {
            netMap.remove(object);
        }
    }

    //注销全部
    public void unRegisterAll() {
        if (!netMap.isEmpty()) {
            netMap.clear();
        }
        netMap = null;
    }
}
