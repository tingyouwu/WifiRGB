package com.wty.app.wifirgb.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.wcolorpicker.android.IOnColorSelectedListener;
import com.wcolorpicker.android.WCircleColorPicker;
import com.wty.app.wifirgb.R;

public class MainActivity extends AppCompatActivity implements IOnColorSelectedListener{

    public static final String BLUETOOTH_ADDRESS = "address";

    View viewColorNew;
    View viewColorOld;
    WCircleColorPicker colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewColorNew = findViewById(R.id.view_color_new);
        viewColorOld = findViewById(R.id.view_color_old);

        colorPicker = (WCircleColorPicker) findViewById(R.id.color_picker);
        colorPicker.setOnColorSelectedListener(this);
    }

    @Override
    public void onColorSelected(int newColor, int oldColor) {
        viewColorNew.setBackgroundColor(newColor);
        viewColorOld.setBackgroundColor(oldColor);
    }

    public static void startMainActivity(Context context,String address){
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(BLUETOOTH_ADDRESS,address);
        context.startActivity(intent);
    }
}
