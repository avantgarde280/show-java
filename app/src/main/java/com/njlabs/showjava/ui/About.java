package com.njlabs.showjava.ui;

import android.os.Bundle;
import android.widget.TextView;

import com.njlabs.showjava.BuildConfig;
import com.njlabs.showjava.R;

public class About extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setupLayout(R.layout.activity_about);
		((TextView) findViewById(R.id.AppVersion)).setText("Version " + BuildConfig.VERSION_NAME);
	}
}
