package com.wty.app.bluetoothlib.ble.exception.handler;

import android.content.Context;
import android.util.Log;

import com.wty.app.bluetoothlib.ble.exception.ConnectException;
import com.wty.app.bluetoothlib.ble.exception.GattException;
import com.wty.app.bluetoothlib.ble.exception.InitiatedException;
import com.wty.app.bluetoothlib.ble.exception.OtherException;
import com.wty.app.bluetoothlib.ble.exception.TimeoutException;

public class DefaultBleExceptionHandler extends BleExceptionHandler {

    private static final String TAG = "BleExceptionHandler";
    private Context context;

    public DefaultBleExceptionHandler(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    protected void onConnectException(ConnectException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onGattException(GattException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onTimeoutException(TimeoutException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onInitiatedException(InitiatedException e) {
        Log.e(TAG, e.getDescription());
    }

    @Override
    protected void onOtherException(OtherException e) {
        Log.e(TAG, e.getDescription());
    }
}
