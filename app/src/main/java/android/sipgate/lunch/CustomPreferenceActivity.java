package android.sipgate.lunch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.sipgate.lunch.sipgatelunch.R;
import android.text.method.DigitsKeyListener;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.Date;


/**
 * Customized preferences activity
 *
 * @author schafm
 */
public class CustomPreferenceActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {
    private static final String TAG = CustomPreferenceActivity.class.getSimpleName();

    // default values
    public static final boolean DEFAULT_TRANSCULENT_NAV_BAR = false;
    public static final boolean DEFAULT_ENABLE_GOOGLE_IMAGE_SEARCH = true;
    public static final String DEFAULT_GOOGLE_IMAGE_SEARCH_KEYWORDS = "essen Foto eatsmarter.de chefkoch.de";

    // keys for the preferences items
    public static final String[] KEY_PREFERENCE = {};

    // keys for the preferences items with password protection
    public static final String[] KEY_PW_PREFERENCE = {};

    private boolean mTimestampChanged = false;

    public static void initFirstRun(Context context) {
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        prefs.edit().putBoolean("transculentNavBar", DEFAULT_TRANSCULENT_NAV_BAR).commit();
        prefs.edit().putBoolean("enableGoogleImageSearch", DEFAULT_ENABLE_GOOGLE_IMAGE_SEARCH).commit();
        prefs.edit().putString("googleImageSearchKeywords", DEFAULT_GOOGLE_IMAGE_SEARCH_KEYWORDS).commit();
        prefs.edit().putBoolean("firstRun", false).commit();
    }

    /**
     * Initialisierungsmethode der Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Lade die xml des Einstellungsdialogs
        addPreferencesFromResource(R.xml.preferences);

        // Erlaube nur Dezimalzahlen zur Eingabe des Serverports
        handleDependencies();
    }

    /**
     * Restrict an edittext preference to numbers only input.
     *
     * @param key key of the edittext preference item
     */
    private void restrictToNumbersOnly(String key) {
        EditText editText = (EditText) ((EditTextPreference) getPreferenceScreen().
                findPreference(key)).getEditText();
        editText.setKeyListener(DigitsKeyListener.getInstance(false, true));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setSummaryForPreference();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        handleDependencies();
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        handleDependencies();
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // a dirty hack to prevent boolean preferences to call a loop when changed
        if (!mTimestampChanged) {
            mTimestampChanged = true;
            sharedPreferences.edit().putLong("timestamp", new Date().getTime()).commit();
        } else {
            mTimestampChanged = false;
        }
        setSummaryForPreference();
        handleDependencies();
    }

    /**
     * handle some settings dependencies
     */
    private void handleDependencies() {
        boolean googleImageSearchEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("enableGoogleImageSearch", DEFAULT_ENABLE_GOOGLE_IMAGE_SEARCH);
        getPreferenceScreen().findPreference("googleImageSearchKeywords").setEnabled(googleImageSearchEnabled);
    }

    /**
     * sets the current value of the preference item as its summary text.
     * <p>
     * currently only the following preference types are supported:
     * - EditTextPreference
     * - ListPreference
     * - EditTextPreference with android:password="true"
     * <p>
     * <p>
     * <b>NOTE:</b><br />
     * only preference items with their key value in the String array KEY_PREFERENCE
     * will be recognized by this method! preference items with passwords or
     * sensitive data need to be put in the KEY_PW_PREFERENCE array.
     */
    private void setSummaryForPreference() {
        for (String prefElement : KEY_PREFERENCE) {
            Preference pref = getPreferenceScreen().findPreference(prefElement);

            // summary for EditTextPreference objects
            if (pref.getClass().equals(EditTextPreference.class)) {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                    if (editTextPref.getText().isEmpty()) {
                        editTextPref.setSummary(editTextPref.getSummary());
                    } else {
                        editTextPref.setSummary(editTextPref.getText().toString());
                    }
            }
            // summary for ListPreference objects
            else if (pref.getClass().equals(ListPreference.class)) {
                ListPreference listPref = (ListPreference) pref;
                if (listPref.getEntry().equals("")) {
                    listPref.setSummary(listPref.getSummary());
                } else {
                    listPref.setSummary(listPref.getEntry().toString());
                }
            }
        }

        // summary for passwort-preferences objects
        for (String prefElement : KEY_PW_PREFERENCE) {
            Preference pref = getPreferenceScreen().findPreference(prefElement);

            // summary for EditTextPreference objects
            if (pref.getClass().equals(EditTextPreference.class)) {
                EditTextPreference editTextPref = (EditTextPreference) pref;
                if (editTextPref.getText().isEmpty()) {
                    editTextPref.setSummary(editTextPref.getSummary());
                } else {
                    editTextPref.setSummary("");
                    for (int i = 0; i < editTextPref.getText().length(); i++) {
                        editTextPref.setSummary(editTextPref.getSummary() + "*");
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            int SUCCESS_RESULT = 1;
            setResult(SUCCESS_RESULT, new Intent());
            finish();
            return true;
        }
        return false;
    }
}
