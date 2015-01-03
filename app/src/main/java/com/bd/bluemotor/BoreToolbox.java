package com.bd.bluemotor;

import android.content.Context;
import android.content.ContextWrapper;
import android.widget.Toast;

public class BoreToolbox extends ContextWrapper {

    public BoreToolbox(Context base){
        super(base);
    }

    /**
     * print with toast
     */
	void print (String text){
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

    /**
     * returns resources value by name
     * http://stackoverflow.com/questions/7493287/android-how-do-i-get-string-from-resources-using-its-name
     */
    public String getStringResourceByName(String aString) {
        String packageName = this.getPackageName();
        int resId = this.getResources()
                .getIdentifier(aString, "string", packageName);
        if (resId == 0) {
            return aString;
        } else {
            return this.getString(resId);
        }
    }

}
