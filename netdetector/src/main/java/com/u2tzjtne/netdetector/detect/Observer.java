package com.u2tzjtne.netdetector.detect;

import com.u2tzjtne.netdetector.entity.NetInfo;
import com.u2tzjtne.netdetector.entity.NetType;

public interface Observer {

    void onConnectionTypeChanged(NetType newNetType);

    void onNetworkConnect(NetInfo networkInfo);

    void onNetworkDisconnect(long networkHandle);
}
