package com.dinicha.tts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.dinicha.tts.entities.Box;
//
public class MainActivity extends Activity {
	private final int QUESTIONS=111;
	private int tileWH=50;
	private int tilePadding=1;
	private int totalCols;
	private int totalRows;
	
	private int selRow;
	private int selCol;
	private int prevRow=-1;
	private int prevCol=-1;
	private int startCell;
	private int endCell;
	private int boxStatus;

	private ArrayList<String> verticalQ;
	private ArrayList<String> horizontalQ;
	private ArrayList<Integer> vNumber;
	private ArrayList<Integer> hNumber;
	private AlertDialog.Builder alert;
	
	private Box[][] box;
	private TextView question;
	//private EditText questionInput;
	private ScrollView questionScroll;
	private ScrollView scrollDown;
	private HorizontalScrollView scrollRight;
	private int wrongWordCount = 0;
	private long endTimer = 600;
	private boolean gameover = false;
    TextView timerTextView;
    AudioManager audioManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
		setContentView(R.layout.activity_main);
		Intent intent = getIntent();
		String i = intent.getStringExtra("number");
		readTTS("tts"+i+".txt");
		readQuestions("question"+i+".txt");
		generateNumber();
		question = (TextView) findViewById(R.id.question);
		questionScroll = (ScrollView) findViewById(R.id.questionScroll);
		scrollDown = (ScrollView) findViewById(R.id.scrollDown);
		scrollRight = (HorizontalScrollView) findViewById(R.id.scrollRight);
		alert = new AlertDialog.Builder(this);
        timerTextView = (TextView) findViewById(R.id.timerView);
        audioManager = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        timerHandler.postDelayed(timerRunnable, 0);
	}
	
	Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {

        @Override
        public void run() {
        	if(endTimer>0&&!gameover){
	        	endTimer--;
	            int seconds = (int)(endTimer % 60);
	            int minutes = (int)(endTimer / 60);
	
	            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
	            if(seconds<10){
	            	audioManager.playSoundEffect(SoundEffectConstants.CLICK);   
	            }
	            timerHandler.postDelayed(this, 1000);
        	}else{
        		if(!gameover)
        		checkAll();
        	}
        }
    };
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent i;
		switch (item.getItemId()) {
			case R.id.menu_back:
				i = new Intent(MainActivity.this,TTSListActivity.class);
				startActivity(i);
				finish();
				return true;
			case R.id.menu_horizontal:
				i = new Intent(MainActivity.this,QuestionsListActivity.class);
				i.putExtra("mendatar",true);
				i.putStringArrayListExtra("questions",horizontalQ);
				i.putIntegerArrayListExtra("number",hNumber);
				startActivityForResult(i,QUESTIONS);
				return true;
			case R.id.menu_vertical:
				i = new Intent(MainActivity.this,QuestionsListActivity.class);
				i.putExtra("mendatar",false);
				i.putStringArrayListExtra("questions",verticalQ);
				i.putIntegerArrayListExtra("number",vNumber);
				startActivityForResult(i,QUESTIONS);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == QUESTIONS) {
			if(resultCode == RESULT_OK){      
				int number=data.getIntExtra("number",1);   
				boolean mendatar=data.getBooleanExtra("mendatar", true);       
				for(int row=0;row<totalRows;row++){
					for(int col=0;col<totalCols;col++) {
						if(box[row][col].number==number&&mendatar){
							selCol = row;
							selRow = col;
							boxStatus = markRight(row,col);
							highlightBox(row,col);
							showKeyboard();
							setQuestion();
							scrollRight.scrollTo(box[row][col].view.getLeft(), 0);
							scrollDown.scrollTo(0,box[row][col].view.getTop());
							return;
						}else if(box[row][col].number==number&&!mendatar){
							selCol = col;
							selRow = row;
							boxStatus = markDown(row,col);
							highlightBox(row,col);
							showKeyboard();
							setQuestion();
							scrollRight.scrollTo(box[row][col].view.getLeft(), 0);
							scrollDown.scrollTo(0,box[row][col].view.getTop());
							return;
						}
					}
				}
			}
			if (resultCode == RESULT_CANCELED) {    
				//Write your code if there's no result
			}
		}
	}//onActivityResult
	
	private void setWordEntered(Box box,char c){
		if(box.wordEntered!=c){
			Log.d("Word Count ",String.valueOf(wrongWordCount));
			
			if(box.wordBase==c)//correct
				wrongWordCount--;
			else if(box.wordEntered!=' ')
				wrongWordCount++;
			box.wordEntered = c;

			if(wrongWordCount==0){
				gameover = true;
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Intent i = new Intent(MainActivity.this,GameOverActivity.class);
				i.putExtra("timeout", false);
				i.putExtra("timeleft", endTimer);
				i.putExtra("v", vNumber.size());
				i.putExtra("h", hNumber.size());
				startActivity(i);
				finish();
			}
		}
		TextView wordView = ((TextView)(box.view.findViewById(R.id.guess)));
		wordView.setVisibility(View.VISIBLE);
		wordView.setText(""+c);
	}
	
	private void setNumber(Box box,int number){
		box.number=number;
		TextView numberView = ((TextView)(box.view.findViewById(R.id.number)));
		numberView.setVisibility(View.VISIBLE);
		numberView.setText(String.valueOf(number));
	}
	
	private void setQuestion(){
		Box pointer = box[selRow][selCol];
		if(pointer.status==Box.HCHECKED){
			pointer = box[selRow][startCell];
			question.setText(horizontalQ.get(hNumber.indexOf(pointer.number)));
			questionScroll.setBackgroundColor(Color.BLUE);
		}else if(pointer.status==Box.VCHECKED){
			pointer = box[startCell][selCol];
			question.setText(verticalQ.get(vNumber.indexOf(pointer.number)));
			questionScroll.setBackgroundColor(Color.GREEN);
		}
	}
	private void highlightBox(int row,int col){
		if(row<0||col<0) return;
		if(row>=totalRows||col>=totalCols) return;
		Box pointer = box[row][col];
		unhighlightBox();
		LinearLayout inside = (LinearLayout)pointer.view.findViewById(R.id.word_color2);
		inside.setBackgroundColor(Color.RED);
		prevRow = row;
		prevCol = col;
	}
	private void unhighlightBox(){
		if(prevRow==-1) return;//no highlighted cell
		Box pointer = box[prevRow][prevCol];
		LinearLayout inside = (LinearLayout)pointer.view.findViewById(R.id.word_color2);
		inside.setBackgroundColor(Color.BLACK);
	}
	
	private void setHMark(Box box){
		LinearLayout inside = (LinearLayout)box.view.findViewById(R.id.word_color);
		inside.setBackgroundColor(Color.BLUE);
		box.status = Box.HCHECKED;
	}
	
	private void setVMark(Box box){
		LinearLayout inside = (LinearLayout)box.view.findViewById(R.id.word_color);
		inside.setBackgroundColor(Color.GREEN);
		box.status = Box.VCHECKED;
	}
	
	private int checkHword(){
		for(int col=startCell;col<totalCols;col++){
			Box pointer = box[selRow][col];
			if(pointer.blank) return 1;
			if(pointer.wordBase!=pointer.wordEntered){
				return 0;
			}
		}
		return 1;
	}
	
	private int checkVword(){
		for(int row=startCell;row<totalRows;row++){
			Box pointer = box[row][selCol];
			if(pointer.blank) return 1;
			if(pointer.wordBase!=pointer.wordEntered){
				return 0;
			}
		}
		return 1;
	}
	
	private void checkAll()
	{
		int vCount=0,hCount=0;
		for(int row=0;row<totalRows;row++)  
	    {    
			for(int col=0;col<totalCols;col++)  
			{
				Box pointer = box[row][col];
				if(pointer.blank) continue;
				TextView numberView = ((TextView)(pointer.view.findViewById(R.id.number)));
				if(numberView.getVisibility()==View.VISIBLE){
					if(row+1<totalRows&&!box[row+1][col].blank){
						startCell = row;
						selCol = col;
						vCount+=checkVword();
					}
					if(col+1<totalCols&&!box[row][col+1].blank){
						startCell = col;
						selRow = row;
						hCount+=checkHword();
					}
				}
			}
	    }
		try {
			Thread.sleep(500);//delay
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Intent i = new Intent(MainActivity.this,GameOverActivity.class);
		i.putExtra("timeout", true);
		i.putExtra("v", vNumber.size());
		i.putExtra("h", hNumber.size());
		i.putExtra("vCount", vCount);
		i.putExtra("hCount", hCount);
		startActivity(i);
		finish();
	}
	
	private void resetColor(){
		for(int row=0;row<totalRows;row++)  
	    {    
			for(int col=0;col<totalCols;col++)  
			{
				LinearLayout inside = (LinearLayout)box[row][col].view.findViewById(R.id.word_color);
				inside.setBackgroundColor(Color.WHITE);

				box[row][col].status = Box.UNCHECKED;
			}
		}
	}
	
	private int markRight(int row,int col){
		if(box[row][col].status==Box.HCHECKED)  {//already horizontal
			if(markDown(row,col)>0) return Box.VCHECKED;
		}
		resetColor();
		//mendatar
		int i=0;
		while(!box[row][col-i].blank){//searh till the blank box on left
			if(col-i==0)break;//left side
			i++;			
		}
		i=col-i;
		if(box[row][i].blank)i++;
		boolean changed=false;
		final int start = i;

		startCell = i;
		if(box[row][i].number!=0)//if its has number
		while(i +1 < totalCols &&!box[row][i+1].blank){
			setHMark(box[row][i+1]);
			i++;
			changed=true;
		}

		endCell = i;
		selRow = row;
		selCol = col;
		int status = Box.UNCHECKED;
		if(changed) {
			setHMark(box[row][start]);
			/*
			box[row][start].view.setOnKeyListener(new View.OnKeyListener() {
				
				@Override
				public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
					// TODO Auto-generated method stub
					setWordEntered(box[selRow][start],(char)arg1);
					return false;
				}
			});*/
			status = Box.HCHECKED;
		}else{
			box[row][col].status = Box.UNCHECKED;
			status =  Box.UNCHECKED;
		}
		return status;
	}
	
	private int markDown(int row,int col){
		if(box[row][col].status==Box.VCHECKED) {//already vertical
			if(markRight(row,col)>0) return Box.HCHECKED;
		}
		resetColor();
		//menurun
		int i=0;
		while(!box[row-i][col].blank){//searh till the blank box on top
			if(row-i==0)break;//top side
			i++;			
		}
		i=row-i;
		if(box[i][col].blank)i++;
		int start = i;
		startCell = start;
		boolean changed=false;
		if(box[i][col].number!=0)//if its has number
		while(i + 1< totalRows &&!box[i][col].blank){
			setVMark(box[i+1][col]);
			i++;
			changed = true;
		}
		endCell = i-1;
		if(changed) {
			setVMark(box[start][col]);
			return Box.VCHECKED;
		}else{
			box[row][col].status = Box.UNCHECKED;
		}
		return Box.UNCHECKED;
	}
	
	private void generateNumber()
	{
		int count=0;
		hNumber = new ArrayList<Integer>();
		vNumber = new ArrayList<Integer>();
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
					hNumber.add(count);
				}
				if(!top&&bottom){//down
					if(pointer.number==0){
						setNumber(pointer,++count);	
					}
					vNumber.add(count);
				}
				
			}
	    }
	    
	}
	
	private void readQuestions(String filename){
		try {
			AssetManager assetManager = getResources().getAssets();
			InputStream is = assetManager.open(filename);
			Reader reader = new InputStreamReader(is);
			BufferedReader bufferedReader = new BufferedReader(reader);
			verticalQ= new ArrayList<String>();
			horizontalQ= new ArrayList<String>();
			String line = null;
			boolean mendatar = true;
			while ((line = bufferedReader.readLine()) != null) {
				
				line = line.trim();
				if(line.compareTo("#")==0) {
					mendatar=false;
					line = bufferedReader.readLine();
					line = line.trim();
				}
				if(mendatar)
					horizontalQ.add(line);
				else
					verticalQ.add(line);
			}
	    } catch (IOException e1) {
	        Log.d("ERROR DETECTED", "ERROR WHILE TRYING TO OPEN FILE");
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
	    	String rowS = "",colS="";
	    	char c;
	    	while((c=(char) in.read())!=' '){//total row
	    		rowS+=c;
	    	}
	    	while((c=(char) in.read())!='\r'){//total col
	    		colS+=c;
	    	}
	    	in.read();//read \n
	    	totalRows=Integer.parseInt(rowS);// convert to integer
	    	totalCols=Integer.parseInt(colS);// convert to integer
	    	String time = "";
	    	while((c=(char) in.read())!='\r'){//total time
	    		time+=c;
	    	}
	    	in.read();//read \n
	    	endTimer=Integer.parseInt(time);// convert to integer
	    	box = new Box[totalRows][totalCols];//inisialisasi
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
					c = (char)in.read();
					if(c=='#') {
						pointer.blank = true;
					}else{
						wrongWordCount++;
						pointer.view.findViewById(R.id.blankImage).setVisibility(View.GONE);
						pointer.view.findViewById(R.id.word_color).setVisibility(View.VISIBLE);
						pointer.wordBase = c;
						TextView guess = (TextView) pointer.view.findViewById(R.id.guess);
						guess.setText(""+c);
						final int curRow=row;final int curCol=col;
						pointer.view.setOnClickListener(
								new View.OnClickListener() {
									@Override
									public void onClick(View view) {
										highlightBox(curRow,curCol);
										boxStatus = markRight(curRow,curCol);
										if(boxStatus==Box.UNCHECKED) boxStatus = markDown(curRow,curCol);
										//if(status!=Box.UNCHECKED) askQuestion();
										showKeyboard();
										selCol = curCol;
										selRow = curRow;
										setQuestion();
									}
								});
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
	public void showKeyboard(){
		final InputMethodManager imm = (InputMethodManager) getBaseContext().getSystemService(Service.INPUT_METHOD_SERVICE);
		//questionInput.requestFocus();
		//imm.showSoftInput(questionInput, InputMethodManager.SHOW_IMPLICIT);
		imm.hideSoftInputFromWindow(question.getWindowToken(), 0);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
	}
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    //alert.setTitle(String.valueOf(keyCode));
	    //alert.show();
		char c = (char)event.getUnicodeChar();
		
		if(keyCode==KeyEvent.KEYCODE_BACK){
			if(boxStatus==Box.HCHECKED){
				if(selCol>startCell){
					selCol--;
					highlightBox(selRow,selCol);
				}
			}else if(boxStatus==Box.VCHECKED){
				if(selRow>startCell){
					selRow--;
					highlightBox(selRow,selCol);
				}
			}
		}else
		if(Character.isLetter(c)){
			if(selRow<0||selRow<0) return true;
			if(selRow>=totalRows||selRow>=totalCols) return true;
			setWordEntered(box[selRow][selCol],Character.toUpperCase(c));
			if(boxStatus==Box.HCHECKED){
				//checkHword();
				if(selCol==endCell){
					unhighlightBox();
				}else{
					selCol++;
					highlightBox(selRow,selCol);
				}
			}else if(boxStatus==Box.VCHECKED){
				//checkVword();
				if(selRow==endCell){
					unhighlightBox();
				}else{
					selRow++;
					highlightBox(selRow,selCol);
				}
			}
		}
		super.onKeyUp(keyCode, event);
		return true;
	}
	 
}
