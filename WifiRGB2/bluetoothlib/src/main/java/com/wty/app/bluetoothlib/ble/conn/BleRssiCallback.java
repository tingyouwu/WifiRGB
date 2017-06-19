package com.wty.app.bluetoothlib.ble.conn;

/**
 * 描述:蓝牙信号强度
 **/

public abstract class BleRssiCallback extends BleCallback {
    public abstract void onSuccess(int rssi);
}