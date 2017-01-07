package com.example.nikhil.message;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Created by nikhil on 14/12/16.
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.settings);
    }
}
