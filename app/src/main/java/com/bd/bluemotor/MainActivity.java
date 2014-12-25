package com.bd.bluemotor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // save some data into settings
        SharedPreferences settings = getSharedPreferences("OMNICON_PREF", MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("UUID", "00001101-0000-1000-8000-00805F9B34FB");
        editor.commit();
        editor.putString("RESPONSE_STARTCHAR", "~");
        editor.commit();
        editor.putString("RESPONSE_ENDCHAR", "#");
        editor.commit();
        editor.putString("DEVICE_NAME", "HC-05");
        editor.commit();

    }

    public void goToAvtoActivity(View view){
    	Intent intent = new Intent(this, AvtoActivity.class);
    	startActivity(intent);
    } 
    
    public void goToTankActivity(View view){
    	Intent intent = new Intent(this, TankActivity.class);
    	startActivity(intent);
    } 
    
    public void goToSplosnoActivity(View view){
    	Intent intent = new Intent(this, SplosnoActivity.class);
    	startActivity(intent);
    }  
}
