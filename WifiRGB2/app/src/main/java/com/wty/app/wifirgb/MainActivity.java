package com.wty.app.wifirgb;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.wcolorpicker.android.IOnColorSelectedListener;
import com.wcolorpicker.android.WCircleColorPicker;

public class MainActivity extends AppCompatActivity implements IOnColorSelectedListener{

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
}
