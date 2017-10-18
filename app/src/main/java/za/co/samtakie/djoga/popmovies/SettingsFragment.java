package za.co.samtakie.djoga.popmovies;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.util.Log;

import za.co.samtakie.djoga.popmovies.data.MovieListContract;
import za.co.samtakie.djoga.popmovies.data.MoviePreferences;
import za.co.samtakie.djoga.popmovies.sync.MovieSyncUtils;

/**
 * Created by CPT on 10/15/2017.
 */

@SuppressWarnings("DefaultFileTemplate")
public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{


    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();


        if (preference instanceof ListPreference) {
            // For list preferences, look up the correct display value in
            // the preference's 'entries' list (since they have separate labels/values).
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            Log.d("Pref index ", String.valueOf(prefIndex));
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);

            }
        } else {
            // For other preferences, set the summary to the value's simple string representation.
            preference.setSummary(stringValue);
        }
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Activity activity = getActivity();
        Preference preference = findPreference(key);
        String fav = sharedPreferences.getString(key, "");


        if (key.equals(getString(R.string.pref_sort_key)) && fav.equals("2")) {

            activity.getContentResolver().notifyChange(MovieListContract.MovieListEntry.CONTENT_URI_FAV, null);
        }else {
            MoviePreferences.getSortOrder(activity);
            MovieSyncUtils.startImmediateSync(activity);
            activity.getContentResolver().notifyChange(MovieListContract.MovieListEntry.CONTENT_URI, null);
        }



        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Add 'general' preferences, defined in the XML file
        addPreferencesFromResource(R.xml.pref_general);

        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // register the preference change listener
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }
}