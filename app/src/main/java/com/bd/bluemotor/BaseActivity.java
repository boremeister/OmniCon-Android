package com.bd.bluemotor;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class BaseActivity extends Activity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){

            case R.id.menu_manual:
                goToManualTestMenu(item);
                break;
            case R.id.menu_about:
                showAboutScreen(item);
                break;
            case R.id.menu_help:
                showHelpScreen(item);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);

    }

    public void goToManualTestMenu(MenuItem item){
        Intent intent = new Intent(this, ManualTestActivity.class);
        startActivity(intent);
    }

    public void showAboutScreen(MenuItem item){

        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void showHelpScreen(MenuItem item){

        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }

}
