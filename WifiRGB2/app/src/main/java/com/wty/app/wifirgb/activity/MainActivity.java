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
import com.wty.app.wifirgb.R;
import com.wty.app.wifirgb.bluetooth.BluetoothChatService;
import com.wty.app.wifirgb.event.BluetoothEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity implements IOnColorChangeListener,IOnColorSelectedListener{

    WCircleColorPicker colorPicker;
    TextView tv_color;
    View view_color;
    private Handler handler;
    private sendThread sendThread;
    private String sendMessage = "";

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
            case BluetoothChatService.MESSAGE_WRITE:
                byte[] writeBuf = (byte[])(event.getHashMap().get(BluetoothChatService.DATA));
                String writeMessage = new String(writeBuf);
                Log.d("wty","result="+writeMessage);
                break;
            case BluetoothChatService.MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), event.getHashMap().get(BluetoothChatService.TOAST).toString(),
                        Toast.LENGTH_SHORT).show();
                break;
            case BluetoothChatService.MESSAGE_READ:
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
        sendMessage(sendMessage);
//        handler.postDelayed(sendThread, 15);
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
                if (BluetoothChatService.getInstance().getState() != BluetoothChatService.STATE_CONNECTED) {
                    LoginActivity.startLoginActivity(MainActivity.this);
                }else {
                    //关闭蓝牙
                    if(BluetoothAdapter.getDefaultAdapter().isEnabled())
                        BluetoothAdapter.getDefaultAdapter().disable();
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
        // Check that we're actually connected before trying anything
        if (BluetoothChatService.getInstance().getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        // Check that there's actually something to send
        if (message.length() > 0) {
            byte[] send = message.getBytes();
            BluetoothChatService.getInstance().write(send);
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
    }

    private class sendThread implements Runnable {
        @Override
        public void run() {
            sendMessage(sendMessage);
        }
    }

}
