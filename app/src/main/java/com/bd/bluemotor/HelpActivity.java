package com.bd.bluemotor;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class HelpActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mi = menu.findItem(R.id.menu_help);
        if(mi != null){
            mi.setEnabled(false);
        }

        return true;
    }

}
