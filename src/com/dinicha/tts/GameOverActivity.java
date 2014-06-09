package com.dinicha.tts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

public class GameOverActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_over);
		findViewById(R.id.btnMain).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent(GameOverActivity.this,SplashActivity.class);
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
		Intent intent = getIntent();
		int v = intent.getIntExtra("v",0);
		int h = intent.getIntExtra("h",0);
		int vCount = intent.getIntExtra("vCount",0);
		int hCount = intent.getIntExtra("hCount",0);
		boolean timeout = intent.getBooleanExtra("timeout", true);

        TextView timerLeftView = (TextView) findViewById(R.id.timeLeft);
        TextView timerTextView = (TextView) findViewById(R.id.timeLeftText);

        TextView hRightView = (TextView) findViewById(R.id.mendatarBenar);
        TextView vRightView = (TextView) findViewById(R.id.menurunBenar);
        TextView hWrongView = (TextView) findViewById(R.id.mendatarSalah);
        TextView vWrongView = (TextView) findViewById(R.id.menurunSalah);
		if(timeout){
			timerTextView.setText("Waktu Habis");
			timerLeftView.setVisibility(View.GONE);

	        hRightView.setText(String.valueOf(hCount)+" Benar, ");
	        vRightView.setText(String.valueOf(vCount)+" Benar, ");
	        hWrongView.setText(String.valueOf(h-hCount)+" Salah");
	        vWrongView.setText(String.valueOf(v-vCount)+" Salah");
		}else{
			long timeleft = intent.getLongExtra("timeleft",0);
			int seconds = (int)(timeleft % 60);
	        int minutes = (int)(timeleft / 60);
	        if(minutes==0){
	        	timerLeftView.setText(String.valueOf(timeleft)+" detik");
	        }else{
	        	timerLeftView.setText(String.valueOf(minutes)+" menit, "+String.valueOf(seconds)+" detik");
	        }

	        hRightView.setText(String.valueOf(h)+" Benar, ");
	        vRightView.setText(String.valueOf(v)+" Benar, ");
	        hWrongView.setText("0 Salah");
	        vWrongView.setText("0 Salah");
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.splash, menu);
		return true;
	}

}
