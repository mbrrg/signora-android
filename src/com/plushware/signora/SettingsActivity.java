package com.plushware.signora;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.Window;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        
        addPreferencesFromResource(R.xml.preferences);
    }
    
    @Override
    public void onStart() {
    	super.onStart();

        EditTextPreference textPref = (EditTextPreference)findPreference("pref_server_url");
        textPref.setSummary(textPref.getText());    	
    	
    	PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }
    
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference pref = findPreference(key);

        if (pref instanceof EditTextPreference) {
            EditTextPreference textPref = (EditTextPreference)pref;
            pref.setSummary(textPref.getText());
        }
    }    
}