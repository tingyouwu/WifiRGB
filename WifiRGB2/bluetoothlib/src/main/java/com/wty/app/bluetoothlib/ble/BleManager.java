package com.wty.app.bluetoothlib.ble;

import android.content.Context;
import android.content.pm.PackageManager;

import com.wty.app.bluetoothlib.ble.conn.BleCharacterCallback;
import com.wty.app.bluetoothlib.ble.conn.BleGattCallback;
import com.wty.app.bluetoothlib.ble.data.ScanResult;
import com.wty.app.bluetoothlib.ble.exception.BleException;
import com.wty.app.bluetoothlib.ble.exception.handler.DefaultBleExceptionHandler;
import com.wty.app.bluetoothlib.ble.scan.ListScanCallback;
import com.wty.app.bluetoothlib.ble.utils.BleLog;


/**
 * Created by chenlijian on 2016/8/17.
 * BLE Manager
 */
public class BleManager {

    private Context mContext;
    private BleBluetooth bleBluetooth;
    private DefaultBleExceptionHandler bleExceptionHandler;

    public BleManager(Context context) {
        this.mContext = context;

        if (bleBluetooth == null) {
            bleBluetooth = new BleBluetooth(context);
        }

        bleExceptionHandler = new DefaultBleExceptionHandler(context);
    }

    /**
     * handle Exception Information
     */
    public void handleException(BleException exception) {
        bleExceptionHandler.handleException(exception);
    }

    /**
     * scan device around
     */
    public boolean scanDevice(ListScanCallback callback) {
        return bleBluetooth.startLeScan(callback);
    }

    /**
     * connect a searched device
     *
     * @param scanResult  searched device
     * @param autoConnect
     * @param callback
     */
    public void connectDevice(ScanResult scanResult,
                              boolean autoConnect,
                              BleGattCallback callback) {
        if (scanResult == null || scanResult.getDevice() == null) {
            if (callback != null) {
                callback.onNotFoundDevice();
            }
        } else {
            if (callback != null) {
                callback.onFoundDevice(scanResult);
            }
            bleBluetooth.connect(scanResult, autoConnect, callback);
        }
    }

    /**
     * cancel scan
     */
    public void cancelScan() {
        bleBluetooth.cancelScan();
    }

    /**
     * notify
     *
     * @param uuid_service
     * @param uuid_notify
     * @param callback
     * @return
     */
    public boolean notify(String uuid_service,
                          String uuid_notify,
                          BleCharacterCallback callback) {
        return bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_notify, null)
                .enableCharacteristicNotify(callback, uuid_notify);
    }

    /**
     * indicate
     *
     * @param uuid_service
     * @param uuid_indicate
     * @param callback
     * @return
     */
    public boolean indicate(String uuid_service,
                            String uuid_indicate,
                            BleCharacterCallback callback) {
        return bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_indicate, null)
                .enableCharacteristicIndicate(callback, uuid_indicate);
    }

    /**
     * write
     *
     * @param uuid_service
     * @param uuid_write
     * @param data
     * @param callback
     * @return
     */
    public boolean writeDevice(String uuid_service,
                               String uuid_write,
                               byte[] data,
                               BleCharacterCallback callback) {
        return bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_write, null)
                .writeCharacteristic(data, callback, uuid_write);
    }

    /**
     * read
     *
     * @param uuid_service
     * @param uuid_read
     * @param callback
     * @return
     */
    public boolean readDevice(String uuid_service,
                              String uuid_read,
                              BleCharacterCallback callback) {
        return bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_read, null)
                .readCharacteristic(callback, uuid_read);
    }

    /**
     * get state
     */
    public void getBluetoothState() {
        BleLog.i("ConnectionState:  " + bleBluetooth.getConnectionState()
                + "\nisInScanning: " + bleBluetooth.isInScanning()
                + "\nisConnected: " + bleBluetooth.isConnected()
                + "\nisServiceDiscovered: " + bleBluetooth.isServiceDiscovered());
    }

    /**
     * refresh Device Cache
     */
    public void refreshDeviceCache() {
        bleBluetooth.refreshDeviceCache();
    }

    /**
     * close gatt
     */
    public void closeBluetoothGatt() {
        if (bleBluetooth != null) {
            bleBluetooth.clearCallback();
            try {
                bleBluetooth.closeBluetoothGatt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * is support ble?
     */
    public boolean isSupportBle() {
        return mContext.getApplicationContext()
                .getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * open bluetooth
     */
    public void enableBluetooth() {
        if (bleBluetooth != null) {
            bleBluetooth.enableBluetoothIfDisabled();
        }
    }

    /**
     * close bluetooth
     */
    public void disableBluetooth() {
        if (bleBluetooth != null) {
            bleBluetooth.disableBluetooth();
        }
    }

    public boolean isBlueEnable() {
        return bleBluetooth != null && bleBluetooth.isBlueEnable();
    }

    public boolean isInScanning() {
        return bleBluetooth.isInScanning();
    }

    public boolean isConnectingOrConnected() {
        return bleBluetooth.isConnectingOrConnected();
    }

    public boolean isConnected() {
        return bleBluetooth.isConnected();
    }

    public boolean isServiceDiscovered() {
        return bleBluetooth.isServiceDiscovered();
    }

    /**
     * remove callback form a character
     */
    public void stopListenCharacterCallback(String uuid) {
        bleBluetooth.removeGattCallback(uuid);
    }

    /**
     * remove callback for gatt connect
     */
    public void stopListenConnectCallback() {
        bleBluetooth.removeConnectGattCallback();
    }

    /**
     * stop notify, remove callback
     */
    public boolean stopNotify(String uuid_service, String uuid_notify) {
        boolean success = bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_notify, null)
                .disableCharacteristicNotify();
        if (success) {
            bleBluetooth.removeGattCallback(uuid_notify);
        }
        return success;
    }

    /**
     * stop indicate, remove callback
     */
    public boolean stopIndicate(String uuid_service, String uuid_indicate) {
        boolean success = bleBluetooth.newBleConnector()
                .withUUIDString(uuid_service, uuid_indicate, null)
                .disableCharacteristicIndicate();
        if (success) {
            bleBluetooth.removeGattCallback(uuid_indicate);
        }
        return success;
    }

}
