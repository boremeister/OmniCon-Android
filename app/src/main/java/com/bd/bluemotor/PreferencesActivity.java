package com.bd.bluemotor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.MultiSelectListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.prefs.Preferences;

/*
* http://stackoverflow.com/questions/531427/how-do-i-display-the-current-value-of-an-android-preference-in-the-preference-su
* http://www.devlog.en.alt-area.org/?p=1209
 */

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        // create reset button
        Button resetBtn = new Button(this);
        resetBtn.setText("Reset");
        resetBtn.setTextColor(Color.WHITE);
        resetBtn.setBackgroundResource(R.drawable.button_red);

        resetBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                PreferenceManager
                        .getDefaultSharedPreferences(getApplicationContext())
                        .edit()
                        .clear()
                        .commit();
                PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preferences, true);

                // redraw view
                restartThis();
            }
        });

        // add reset button to settings
        ListView v = getListView();
        v.addFooterView(resetBtn);

        PreferenceManager.setDefaultValues(this, R.xml.preferences,
                false);
        initSummary(getPreferenceScreen());

    }

    @Override
    protected void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        updatePrefSummary(findPreference(key));
    }

    private void initSummary(Preference p) {
        if (p instanceof PreferenceGroup) {
            PreferenceGroup pGrp = (PreferenceGroup) p;
            for (int i = 0; i < pGrp.getPreferenceCount(); i++) {
                initSummary(pGrp.getPreference(i));
            }
        } else {
            updatePrefSummary(p);
        }
    }

    private void updatePrefSummary(Preference p) {
        if (p instanceof ListPreference) {
            ListPreference listPref = (ListPreference) p;
            p.setSummary(listPref.getEntry());
        }
        if (p instanceof EditTextPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            if (p.getTitle().toString().contains("password"))
            {
                p.setSummary("******");
            } else {
                p.setSummary(editTextPref.getText());
            }
        }
        if (p instanceof MultiSelectListPreference) {
            EditTextPreference editTextPref = (EditTextPreference) p;
            p.setSummary(editTextPref.getText());
        }
    }

    private void restartThis() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_base, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mi = menu.findItem(R.id.menu_preferences);
        if(mi != null){
            mi.setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){

            case R.id.menu_manual_control:
                goToManualTestMenu();
                break;
            case R.id.menu_preferences:
                showPreferencesScreen();
                break;
            case R.id.menu_about:
                showAboutScreen();
                break;
            case R.id.menu_help:
                showHelpScreen();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return super.onOptionsItemSelected(item);

    }

    public void goToManualTestMenu(){
        Intent intent = new Intent(this, ManualTestActivity.class);
        startActivity(intent);
    }

    public void showPreferencesScreen(){
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void showAboutScreen(){
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    public void showHelpScreen(){

        Intent intent = new Intent(this, HelpActivity.class);
        startActivity(intent);
    }
}
