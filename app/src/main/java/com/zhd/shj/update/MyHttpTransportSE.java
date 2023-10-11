package com.zhd.shj.update;

import org.ksoap2.transport.HttpTransportSE;
import org.ksoap2.transport.ServiceConnection;
import org.ksoap2.transport.ServiceConnectionSE;

import java.io.IOException;

public class MyHttpTransportSE extends HttpTransportSE {

    private int timeout = 30000; // 默认超时时间为30s

    public MyHttpTransportSE(String url) {
        super(url);
    }

    public MyHttpTransportSE(String url, int timeout) {
        super(url);
        this.timeout = timeout;
    }

    @Override
    public ServiceConnection getServiceConnection() throws IOException {
        ServiceConnectionSE serviceConnection = new ServiceConnectionSE(url);


        return serviceConnection;
    }
}
