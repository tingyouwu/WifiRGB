package com.wty.app.wifirgb.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.wcolorpicker.android.IOnColorChangeListener;
import com.wcolorpicker.android.IOnColorSelectedListener;
import com.wcolorpicker.android.WCircleColorPicker;
import com.wty.app.bluetoothlib.event.BluetoothEvent;
import com.wty.app.bluetoothlib.hc.HcBluetoothService;
import com.wty.app.wifirgb.R;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements IOnColorChangeListener,IOnColorSelectedListener{

    WCircleColorPicker colorPicker;
    TextView tv_color;
    TextView tv_data;
    View view_color;
    private Handler handler;
    private sendThread sendThread;
    private String sendMessage = "";
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
        colorPicker = (WCircleColorPicker) findViewById(R.id.color_picker);
        colorPicker.setOnColorChangedListener(this);
        colorPicker.setOnColorSelectedListener(this);
        view_color = findViewById(R.id.color_view);
        tv_color = (TextView) findViewById(R.id.color_tv);
        tv_data = (TextView) findViewById(R.id.tv_send);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(sendThread == null){
            sendThread = new sendThread();
        }
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMainThread(BluetoothEvent event){
        switch (event.getType()){
            case HcBluetoothService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[])(event.getHashMap().get(HcBluetoothService.DATA));
                break;
            case HcBluetoothService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), event.getHashMap().get(HcBluetoothService.TOAST).toString(),
                        Toast.LENGTH_SHORT).show();
                break;
            case HcBluetoothService.MESSAGE_READ:
                break;
            default:
                break;
        }
    }

    @Override
    public void onColorSelected(int red, int green, int blue) {
        tv_color.setText("R:"+red+",G:"+green+",B:"+blue);
        getSendMessage(red,green,blue);
        handler.removeCallbacks(sendThread);
        handler.postDelayed(sendThread, 5);
    }

    @Override
    public void onColorSelected(int newColor, int oldColor) {
        view_color.setBackgroundColor(newColor);
    }

    public static void startMainActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (HcBluetoothService.getInstance().getState() != HcBluetoothService.STATE_CONNECTED) {
                    LoginActivity.startLoginActivity(MainActivity.this);
                }else {
                    //关闭蓝牙
                    if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled())
                        mBluetoothAdapter.disable();
                }
                finish();
                break;
            default:
                return super.onKeyDown(keyCode, event);
        }
        return false;
    }

    /**
     * 发送数据
     * @param message A string of text to send.
     */
    private void sendMessage(String message) {

        if (mBluetoothAdapter == null) {
            return;
        }

        if (HcBluetoothService.getInstance().getState() != HcBluetoothService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            Log.d("_RGB_",message);
            byte[] send = message.getBytes();
            HcBluetoothService.getInstance().write(send);
        }
    }

    /**
     * @Desc 把RGB组合成协议内的数据格式
     *  开始符+长度+rgb+结束符
     * "+C,n:rgbString;"
     * 例子:"+C,10:100,255,60;"
     **/
    public void getSendMessage(int red,int green,int blue){
        StringBuilder rgbString = new StringBuilder();
        rgbString.append(""+red).append(",").append(""+green).append(",").append(""+blue);
        StringBuilder sb = new StringBuilder();
        sb.append("+C,").append(""+rgbString.toString().length()).append(":").append(rgbString.toString()).append(";");
        Log.d("wty","data:"+sb.toString());
        sendMessage = sb.toString();
        tv_data.setText("协议数据："+sendMessage);
    }

    private class sendThread implements Runnable {
        @Override
        public void run() {
            sendMessage(sendMessage);
        }
    }

}
