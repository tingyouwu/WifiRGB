package com.wty.app.bluetoothlib.event;

import java.util.HashMap;

/**
 * @Desc 蓝牙EventBus 事件
 **/
public class BluetoothEvent {

    private int type;

    private HashMap<String, Object> hashMap;

    public BluetoothEvent(int type){
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public HashMap<String, Object> appendHashParam(String key, Object object) {
        if (hashMap == null) {
            hashMap = new HashMap<>();
        }
        hashMap.put(key, object);
        return hashMap;
    }

    public HashMap<String, Object> getHashMap() {
        return hashMap;
    }
}
