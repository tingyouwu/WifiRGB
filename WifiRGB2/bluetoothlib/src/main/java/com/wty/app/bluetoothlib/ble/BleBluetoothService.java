package com.wty.app.bluetoothlib.ble;

import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;

import com.wty.app.bluetoothlib.ble.callback.BleConnectCallback;
import com.wty.app.bluetoothlib.ble.callback.BleScanCallback;
import com.wty.app.bluetoothlib.ble.conn.BleCharacterCallback;
import com.wty.app.bluetoothlib.ble.conn.BleGattCallback;
import com.wty.app.bluetoothlib.ble.data.ScanResult;
import com.wty.app.bluetoothlib.ble.exception.BleException;
import com.wty.app.bluetoothlib.ble.scan.ListScanCallback;
import com.wty.app.bluetoothlib.ble.utils.HexUtil;

/**
 * 描述：蓝牙服务
 **/

public class BleBluetoothService extends Service {
    public BluetoothBinder mBinder = new BluetoothBinder();
    private BleManager bleManager;
    private Handler threadHandler = new Handler(Looper.getMainLooper());
    private BleScanCallback scanCallback;
    private BleConnectCallback connectCallback;

    private String name;
    private String mac;
    private BluetoothGatt gatt;
    private BluetoothGattService service;
    private BluetoothGattCharacteristic characteristic;
    private int charaProp;

    @Override
    public void onCreate() {
        bleManager = new BleManager(this);
        bleManager.enableBluetooth();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bleManager = null;
        scanCallback = null;
        connectCallback = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        bleManager.closeBluetoothGatt();
        return super.onUnbind(intent);
    }

    public class BluetoothBinder extends Binder {
        public BleBluetoothService getService() {
            return BleBluetoothService.this;
        }
    }

    public void setScanCallback(BleScanCallback scanCallback) {
        this.scanCallback = scanCallback;
    }

    public void setConnectCallback(BleConnectCallback connectCallback){
        this.connectCallback = connectCallback;
    }

    /**
     * 扫描设备
     **/
    public void scanDevice() {
        resetInfo();

        if (scanCallback != null) {
            scanCallback.onStartScan();
        }

        boolean b = bleManager.scanDevice(new ListScanCallback(15000) {

            @Override
            public void onScanning(final ScanResult result) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scanCallback != null) {
                            scanCallback.onScanning(result);
                        }
                    }
                });
            }

            @Override
            public void onScanComplete(final ScanResult[] results) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (scanCallback != null) {
                            scanCallback.onScanComplete();
                        }
                    }
                });
            }
        });
        if (!b) {
            if (scanCallback != null) {
                scanCallback.onScanComplete();
            }
        }
    }

    public void cancelScan() {
        bleManager.cancelScan();
    }

    /**
     * 连接设备
     **/

    public void connectDevice(final ScanResult scanResult) {
        if (connectCallback != null) {
            connectCallback.onConnecting();
        }

        bleManager.connectDevice(scanResult, true, new BleGattCallback() {
            @Override
            public void onNotFoundDevice() {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectCallback != null) {
                            connectCallback.onConnectFail();
                        }
                    }
                });
            }

            @Override
            public void onFoundDevice(ScanResult scanResult) {
                BleBluetoothService.this.name = scanResult.getDevice().getName();
                BleBluetoothService.this.mac = scanResult.getDevice().getAddress();
            }

            @Override
            public void onConnectSuccess(BluetoothGatt gatt, int status) {
                gatt.discoverServices();
            }

            @Override
            public void onServicesDiscovered(final BluetoothGatt gatt, int status) {
                BleBluetoothService.this.gatt = gatt;
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectCallback != null) {
                            connectCallback.onServicesDiscovered();
                        }
                    }
                });
            }

            @Override
            public void onConnectFailure(BleException exception) {
                runOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        if (connectCallback != null) {
                            connectCallback.onDisConnected();
                        }
                    }
                });
            }
        });
    }

    public void read(String uuid_service, String uuid_read, BleCharacterCallback callback) {
        bleManager.readDevice(uuid_service, uuid_read, callback);
    }

    public void write(String uuid_service, String uuid_write, String hex, BleCharacterCallback callback) {
        bleManager.writeDevice(uuid_service, uuid_write, HexUtil.hexStringToBytes(hex), callback);
    }

    public void notify(String uuid_service, String uuid_notify, BleCharacterCallback callback) {
        bleManager.notify(uuid_service, uuid_notify, callback);
    }

    public void indicate(String uuid_service, String uuid_indicate, BleCharacterCallback callback) {
        bleManager.indicate(uuid_service, uuid_indicate, callback);
    }

    public void stopNotify(String uuid_service, String uuid_notify) {
        bleManager.stopNotify(uuid_service, uuid_notify);
    }

    public void stopIndicate(String uuid_service, String uuid_indicate) {
        bleManager.stopIndicate(uuid_service, uuid_indicate);
    }

    public void closeConnect() {
        bleManager.closeBluetoothGatt();
    }

    private void resetInfo() {
        name = null;
        mac = null;
        gatt = null;
        service = null;
        characteristic = null;
        charaProp = 0;
    }

    public String getName() {
        return name;
    }

    public String getMac() {
        return mac;
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setService(BluetoothGattService service) {
        this.service = service;
    }

    public BluetoothGattService getService() {
        return service;
    }

    public void setCharacteristic(BluetoothGattCharacteristic characteristic) {
        this.characteristic = characteristic;
    }

    public BluetoothGattCharacteristic getCharacteristic() {
        return characteristic;
    }

    public void setCharaProp(int charaProp) {
        this.charaProp = charaProp;
    }

    public int getCharaProp() {
        return charaProp;
    }

    private void runOnMainThread(Runnable runnable) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            runnable.run();
        } else {
            threadHandler.post(runnable);
        }
    }
}
