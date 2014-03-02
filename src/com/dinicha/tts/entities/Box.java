package com.dinicha.tts.entities;

import android.widget.LinearLayout;

public class Box {
	public static final int UNCHECKED=0;
	public static final int HCHECKED=1;
	public static final int VCHECKED=2;
	public int number=0;//no pertanyaan
	public int status=UNCHECKED;
	public boolean blank=false;//status
	public char wordBase=' ';//word
	public char wordEntered=' ';//word
	public LinearLayout view=null;
}
