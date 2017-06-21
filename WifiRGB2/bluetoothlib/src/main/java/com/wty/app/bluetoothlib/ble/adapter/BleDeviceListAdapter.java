package com.wty.app.bluetoothlib.ble.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.TextView;

import com.wty.app.bluetoothlib.R;
import com.wty.app.bluetoothlib.ble.data.ScanResult;
import com.wty.app.bluetoothlib.util.RssiUtil;

import java.util.List;

/**
 * 功能描述：通用 adapter
 * @author wty
 */
public class BleDeviceListAdapter extends BaseViewCommonAdapter<ScanResult> {

    public BleDeviceListAdapter(Context context, List<ScanResult> data){
        super(context, R.layout.device_name_ble,data);
    }

    @Override
    protected void convert(BaseViewHolder holder, ScanResult item, int position) {
        TextView tv_name = holder.getView(R.id.tv_name);
        TextView tv_distance = holder.getView(R.id.tv_distance);
        TextView tv_mac = holder.getView(R.id.tv_mac);

        if(TextUtils.isEmpty(item.getDevice().getName())){
            tv_name.setText("未知");
        }else{
            tv_name.setText(item.getDevice().getName());
        }

        tv_distance.setText("距离："+ RssiUtil.getDistance(item.getRssi())+"m");
        tv_mac.setText("MAC地址："+item.getDevice().getAddress());
    }
}
