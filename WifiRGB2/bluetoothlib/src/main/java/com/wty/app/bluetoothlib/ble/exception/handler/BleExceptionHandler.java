package com.wty.app.bluetoothlib.ble.exception.handler;


import com.wty.app.bluetoothlib.ble.exception.BleException;
import com.wty.app.bluetoothlib.ble.exception.ConnectException;
import com.wty.app.bluetoothlib.ble.exception.GattException;
import com.wty.app.bluetoothlib.ble.exception.InitiatedException;
import com.wty.app.bluetoothlib.ble.exception.OtherException;
import com.wty.app.bluetoothlib.ble.exception.TimeoutException;

public abstract class BleExceptionHandler {

    public BleExceptionHandler handleException(BleException exception) {
        if (exception != null) {
            if (exception instanceof ConnectException) {
                onConnectException((ConnectException) exception);
            } else if (exception instanceof GattException) {
                onGattException((GattException) exception);
            } else if (exception instanceof TimeoutException) {
                onTimeoutException((TimeoutException) exception);
            } else if (exception instanceof InitiatedException) {
                onInitiatedException((InitiatedException) exception);
            } else {
                onOtherException((OtherException) exception);
            }
        }
        return this;
    }

    /**
     * connect failed
     */
    protected abstract void onConnectException(ConnectException e);

    /**
     * gatt error status
     */
    protected abstract void onGattException(GattException e);

    /**
     * operation timeout
     */
    protected abstract void onTimeoutException(TimeoutException e);

    /**
     * operation inititiated error
     */
    protected abstract void onInitiatedException(InitiatedException e);

    /**
     * other exceptions
     */
    protected abstract void onOtherException(OtherException e);
}
