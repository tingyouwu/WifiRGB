package com.wty.app.bluetoothlib.ble.callback;

/**
 * 描述:蓝牙连接回调
 **/

public interface BleConnectCallback {

    void onConnecting();//提示正在连接

    void onConnectFail();//连接失败  找不到设备

    void onDisConnected();//断开连接

    void onServicesDiscovered();//发现服务

}
