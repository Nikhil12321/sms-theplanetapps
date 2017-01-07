package com.example.nikhil.message;

import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by nikhil on 14/12/16.
 */

public class Settings extends PreferenceActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment()).commit();
    }
}
