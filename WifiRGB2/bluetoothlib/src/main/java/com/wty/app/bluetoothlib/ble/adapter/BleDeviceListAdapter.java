package com.wty.app.bluetoothlib.ble.adapter;

import android.content.Context;
import android.widget.TextView;

import com.wty.app.bluetoothlib.R;
import com.wty.app.bluetoothlib.ble.data.ScanResult;

import java.util.List;

/**
 * 功能描述：通用 adapter
 * @author wty
 */
public class BleDeviceListAdapter extends BaseViewCommonAdapter<ScanResult> {

    public BleDeviceListAdapter(Context context, List<ScanResult> data){
        super(context, R.layout.device_name,data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ScanResult item, int position) {
        TextView tv_name = holder.getView(R.id.tv_name);
        tv_name.setText(item.getDevice().getName()+"\n" + item.getDevice().getAddress());
    }
}
