package com.bd.bluemotor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

// TODO: create handler (class) for shared preferences

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
