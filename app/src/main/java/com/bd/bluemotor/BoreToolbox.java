package com.bd.bluemotor;

import android.content.Context;
import android.widget.Toast;

public class BoreToolbox {

	Context c = null;

    // init - set context
	public BoreToolbox(Context c){
		this.c = c.getApplicationContext(); 
	}

    /**
     *
     * print with toast
     */
	void print (String text){
		if(c!=null)
			Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

    /**
     * returns resources value by name
     * http://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
     */
    public String getStringResourceByName(String aString) {
        String packageName = c.getPackageName();
        int resId = c.getResources()
                .getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return c.getString(resId);
        }
    }

}
