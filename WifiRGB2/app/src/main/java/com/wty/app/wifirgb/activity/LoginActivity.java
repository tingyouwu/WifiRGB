package com.wty.app.wifirgb.activity;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.tbruyelle.rxpermissions.RxPermissions;
import com.wty.app.wifirgb.R;
import com.wty.app.wifirgb.bluetooth.BluetoothChatService;
import com.wty.app.wifirgb.bluetooth.DeviceListActivity;
import com.wty.app.wifirgb.event.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Timer;
import java.util.TimerTask;

import rx.functions.Action1;

/**
 * @Desc 连接蓝牙台灯页面
 * @author wty
 **/
public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    Button btn_connect;
    Button about;
    private BluetoothAdapter mBluetoothAdapter = null;

    public static void startLoginActivity(Context context){
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_connect = (Button) findViewById(R.id.btn_connect);
        about = (Button) findViewById(R.id.about);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
        }else{
            if(!mBluetoothAdapter.isEnabled()){
                if (Build.VERSION.SDK_INT >= 23) {
                    RxPermissions.getInstance(LoginActivity.this)
                            .request(Manifest.permission.ACCESS_COARSE_LOCATION,
                                    Manifest.permission.ACCESS_FINE_LOCATION)
                            .subscribe(new Action1<Boolean>() {
                                @Override
                                public void call(Boolean aBoolean) {
                                    if (aBoolean) {
                                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                                        startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                                    }
                                }
                            });
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
            }
        }
        btn_connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(LoginActivity.this, "手机无蓝牙设备", Toast.LENGTH_SHORT).show();
                    return;
                }
                Intent serverIntent = new Intent(LoginActivity.this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,AboutActivity.class);
                startActivity(intent);
            }
        });
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BluetoothEvent event){
        switch (event.getType()){
            case BluetoothChatService.MESSAGE_STATE_CHANGE:
                switch ((int)(event.getHashMap().get(BluetoothChatService.STATE))){
                    case BluetoothChatService.STATE_CONNECTED:
                        break;
                    case BluetoothChatService.STATE_CONNECTING:
                        Toast.makeText(getApplicationContext(), "正在连接该蓝牙台灯", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothChatService.STATE_LISTEN:
                        break;
                    case BluetoothChatService.STATE_NONE:
                        break;
                    default:
                        break;
                }
                break;
            case BluetoothChatService.MESSAGE_DEVICE_NAME:
                String mConnectedDeviceName = event.getHashMap().get(BluetoothChatService.DEVICE_NAME).toString();
                Toast.makeText(getApplicationContext(), "连接上 "
                        + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                MainActivity.startMainActivity(LoginActivity.this);
                finish();
                break;

            case BluetoothChatService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), event.getHashMap().get(BluetoothChatService.TOAST).toString(),
                        Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()){
            mBluetoothAdapter.disable();
        }
        EventBus.getDefault().unregister(this);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                exitBy2Click();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case REQUEST_CONNECT_DEVICE:
                if (resultCode == Activity.RESULT_OK) {
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address);
                    BluetoothChatService.getInstance().connect(device);
                }
                break;

            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, R.string.bt_enabled_success, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    /**
     * 双击退出函数
     */
    private static Boolean isExit = false;
    private void exitBy2Click() {
        Timer tExit;
        if (!isExit) {
            isExit = true;
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

        } else {
            BluetoothChatService.getInstance().stop();
            finish();
        }
    }

}
