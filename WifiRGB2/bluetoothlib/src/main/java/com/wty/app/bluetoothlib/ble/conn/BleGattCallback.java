
package com.wty.app.bluetoothlib.ble.conn;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;

import com.wty.app.bluetoothlib.ble.data.ScanResult;
import com.wty.app.bluetoothlib.ble.exception.BleException;

public abstract class BleGattCallback extends BluetoothGattCallback {

    public abstract void onNotFoundDevice();

    public abstract void onFoundDevice(ScanResult scanResult);

    public abstract void onConnectSuccess(BluetoothGatt gatt, int status);

    public abstract void onConnectFailure(BleException exception);

}