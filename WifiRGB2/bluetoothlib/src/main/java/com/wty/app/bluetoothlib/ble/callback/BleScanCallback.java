package com.wty.app.bluetoothlib.ble.callback;

import com.wty.app.bluetoothlib.ble.data.ScanResult;

/**
 * 描述:蓝牙扫描回调
 **/
public interface BleScanCallback {

    void onStartScan();//扫描开始

    void onScanning(ScanResult scanResult);//获取扫描结果

    void onScanComplete();//扫描结束

}
