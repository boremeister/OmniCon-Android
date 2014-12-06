package com.bd.bluemotor;

import android.content.Context;
import android.widget.Toast;

public class BoreToolbox {

	Context c = null;
	
	public BoreToolbox(Context c){
		this.c = c.getApplicationContext(); 
	}
	
	void print (String text){
		if(c!=null)
			Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}
	
}
