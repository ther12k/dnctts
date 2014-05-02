package com.dinicha.tts;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TTSListActivity extends ListActivity  {

	public void onCreate(Bundle icicle) {
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(icicle);
	    String[] values = new String[] { "TTS1","TTS2","TTS3","TTS4","TTS5","TTS6"};
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	  }

	  @Override
	  protected void onListItemClick(ListView l, View v, int position, long id) {
		  Intent i = new Intent(TTSListActivity.this,MainActivity.class);
		  i.putExtra("number", String.valueOf(position+1));
		  startActivity(i);
		  finish();
	  }

}
