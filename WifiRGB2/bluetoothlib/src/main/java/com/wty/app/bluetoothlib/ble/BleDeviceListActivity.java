package com.wty.app.bluetoothlib.ble;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.wty.app.bluetoothlib.R;
import com.wty.app.bluetoothlib.ble.adapter.BleDeviceListAdapter;
import com.wty.app.bluetoothlib.ble.callback.BleConnectCallback;
import com.wty.app.bluetoothlib.ble.callback.BleScanCallback;
import com.wty.app.bluetoothlib.ble.data.ScanResult;

import java.util.ArrayList;
import java.util.List;

/**
 * 描述：只获取蓝牙ble设备列表
 **/
public class BleDeviceListActivity extends Activity {
	public static String EXTRA_DEVICE_ADDRESS = "device_address";

	private BleBluetoothService mBleBluetoothService;
	private BleDeviceListAdapter adapter;
	private List<ScanResult> data = new ArrayList<>();
	private Button scanButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.device_list_ble);
		setTitle("当前蓝牙设备");
		setResult(Activity.RESULT_CANCELED);
		scanButton = (Button) findViewById(R.id.button_scan);
		scanButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doDiscovery();
				v.setVisibility(View.GONE);
			}
		});

		adapter = new BleDeviceListAdapter(this,data);

		ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
		newDevicesListView.setAdapter(adapter);
		newDevicesListView.setOnItemClickListener(mDeviceClickListener);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBleBluetoothService != null)
			unbindService();
	}

	private void bindService() {
		Intent bindIntent = new Intent(this, BleBluetoothService.class);
		this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
	}

	private void unbindService() {
		this.unbindService(mFhrSCon);
	}

	private ServiceConnection mFhrSCon = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mBleBluetoothService = ((BleBluetoothService.BluetoothBinder) service).getService();
			mBleBluetoothService.setScanCallback(scanCallback);
			mBleBluetoothService.setConnectCallback(connectCallback);
			mBleBluetoothService.scanDevice();
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			mBleBluetoothService = null;
		}
	};

	private BleScanCallback scanCallback = new BleScanCallback() {
		@Override
		public void onStartScan() {
			adapter.clear();
			setProgressBarIndeterminateVisibility(true);
			setTitle(R.string.scanning);
		}

		@Override
		public void onScanning(ScanResult result) {
			adapter.add(result);
		}

		@Override
		public void onScanComplete() {
			setProgressBarIndeterminateVisibility(false);
			setTitle(R.string.select_device);
			scanButton.setVisibility(View.VISIBLE);
		}
	};

	private BleConnectCallback connectCallback = new BleConnectCallback() {
		@Override
		public void onConnecting() {

		}

		@Override
		public void onConnectFail() {

		}

		@Override
		public void onDisConnected() {

		}

		@Override
		public void onServicesDiscovered() {

		}
	};

	/**
	 * 搜索蓝牙设备
	 */
	private void doDiscovery() {
		if(mBleBluetoothService == null){
			bindService();
		}else{
			mBleBluetoothService.scanDevice();
		}
	}

	private OnItemClickListener mDeviceClickListener = new OnItemClickListener()
	{
		public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
			// 取消搜索 并且连接设备
			String info = ((TextView) v).getText().toString();
			String address = info.substring(info.length() - 17);
			Intent intent = new Intent();
			intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};

}
