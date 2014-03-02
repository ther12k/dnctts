package com.dinicha.tts;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dinicha.tts.entities.Box;

public class TestReadTTSActivity extends Activity {
	private int tileWH=50;
	private int tilePadding=1;
	private int totalCols;
	private int totalRows;
	
	private Box[][] box;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		readTTS("tts1.txt");
		generateNumber();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	private void setNumber(Box box,int number){
		box.number=number;
		TextView numberView = ((TextView)(box.view.findViewById(R.id.number)));
		numberView.setVisibility(View.VISIBLE);
		numberView.setText(String.valueOf(number));
	}
	
	private void generateNumber()
	{
		int count=0;

		for(int row=0;row<totalRows;row++)  
	    {    
			for(int col=0;col<totalCols;col++)  
			{
				Box pointer = box[row][col];
				if(pointer.blank) continue;
				boolean left=false,top=false,bottom=false,right=false;
				if(col>0&&!box[row][col-1].blank){
					left=true;//has left box
				}
				if(col+1<totalCols&&!box[row][col+1].blank){
					right=true;//has right box
				}
				if(row>0&&!box[row-1][col].blank){
					top=true;//has top box
				}
				if(row+1<totalRows&&!box[row+1][col].blank){
					bottom=true;//has bottom box
				}
				
				if(!left&&right){//horizontal
					setNumber(pointer,++count);
				}else if(!top&&bottom){//down
					if(pointer.number==0){
						setNumber(pointer,++count);	
					}
				}
				
			}
	    }
	    
	}
	
	private void readTTS(String filename){
		AssetManager assetManager = getResources().getAssets();
		InputStream input = null;
	    InputStreamReader in = null;
	    try {
	        input = assetManager.open(filename);
	        in = new InputStreamReader(input);
	    } catch (IOException e1) {
	        Log.d("ERROR DETECTED", "ERROR WHILE TRYING TO OPEN FILE");
	    }
	    try {
	    	char rowS = (char) in.read();//row
	    	in.read();//space
	    	char colS = (char) in.read();//col
	    	totalRows=rowS-'0';
	    	totalCols=colS-'0';
	    	box = new Box[totalRows][totalCols];
	    	in.read();in.read();//read \r\n
	    	TableLayout table = (TableLayout)(findViewById(R.id.crosswordLayout));
			//table.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		    //for every row
		    for(int row=0;row<totalRows;row++)
		    {
				//create a new table row
				TableRow tableRow = new TableRow(this);
				//set the height and width of the row
				tableRow.setLayoutParams(new LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));
			      
				//for every column
				for(int col=0;col<totalCols;col++) {
					box[row][col] = new Box();
					Box pointer= box[row][col];
					LinearLayout view = (LinearLayout) LayoutInflater.from(getBaseContext()).inflate(R.layout.word,null);

					pointer.view = view;
					pointer.view.setPadding(tilePadding, tilePadding, tilePadding, tilePadding);
					char c = (char)in.read();
					if(c=='#') {
						pointer.view.setVisibility(View.INVISIBLE);
						pointer.blank = true;
					}else{
						pointer.wordBase = c;
						TextView guess = (TextView) pointer.view.findViewById(R.id.guess);
						guess.setText(""+c);
					}
					//add the tile to the table row
					tableRow.addView(pointer.view);			   
				}
				in.read();in.read();//read \r\n
				table.addView(tableRow,new TableLayout.LayoutParams((tileWH * tilePadding) * totalCols, tileWH * tilePadding));  

		    }
	    } catch (IOException e) {
	    	e.printStackTrace();
	    }
	}
}
