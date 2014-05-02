package com.dinicha.tts;

import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class QuestionsListActivity extends ListActivity  {
	private boolean mendatar=true;
	List<String> questions;
	List<Integer> number;
	String[] values;
	public void onCreate(Bundle bundle) {
		getWindow().setSoftInputMode(
			      WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(bundle);
		Bundle bundleExtra = getIntent().getExtras();

		questions = bundleExtra.getStringArrayList("questions");
		number = bundleExtra.getIntegerArrayList("number");
		values = new String[questions.size()];
		int i=0;
		for(String q:questions){
			values[i] = String.valueOf(number.get(i))+". "+q;
			i++;
		}
		mendatar = bundleExtra.getBoolean("mendatar");
	    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
	        android.R.layout.simple_list_item_1, values);
	    setListAdapter(adapter);
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent returnIntent = new Intent();
		returnIntent.putExtra("number", number.get(position));
		returnIntent.putExtra("mendatar", mendatar);
		setResult(RESULT_OK, returnIntent);       
		finish();
	}

}
