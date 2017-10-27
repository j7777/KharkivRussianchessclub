package ua.kh.ruschess.kharkivrussianchessclub.Activity;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

import ua.kh.ruschess.kharkivrussianchessclub.R;

public class SettingsActivity extends PreferenceActivity {
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
    }
}
