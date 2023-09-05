package com.example.sekostream

import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceActivity

class SettingsActivity : PreferenceActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Loads the XML preferences file
        addPreferencesFromResource(R.xml.root_preferences)
    }

    override fun onResume() {
        super.onResume()

        // Registers a listener whenever a key changes
        preferenceScreen?.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()

        // Unregisters the listener set in onResume().
        preferenceScreen?.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }

    // Sets the refreshDisplay flag to "true" to indicate that the main activity should update its display.
    // The main activity queries the PreferenceManager to get the latest settings.

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {

        // Sets refreshDisplay to true so that when the user returns to the main
        // activity, the display refreshes to reflect the new settings.
        //MainActivity.refreshDisplay = true
    }
}