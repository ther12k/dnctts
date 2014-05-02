package com.dinicha.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		findViewById(R.id.btnMain).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(SplashActivity.this,TTSListActivity.class);
						startActivity(intent);
						finish();
					}
				});
		findViewById(R.id.btnHelp).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(SplashActivity.this,HelpActivity.class);
						startActivity(intent);
						finish();
					}
				});
		findViewById(R.id.btnExit).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						finish();
					}
				});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
