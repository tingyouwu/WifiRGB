package com.wty.app.wifirgb.base;

import android.app.Application;

import com.wty.app.wifirgb.util.PreferenceUtil;

public class MyApplication extends Application {

	@Override
	public void onCreate() {
		super.onCreate();
		PreferenceUtil.init(this);
	}

}
