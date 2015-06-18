package android.sipgate.lunch;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import android.sipgate.lunch.ormlite.DatabaseHelper;

/**
 * Customized version of the Application class to store some public static variables.
 *
 * @author schafm
 */
public class CustomApplication extends Application {
    public static final boolean TEST_MODE = false;

    private static DatabaseHelper sDbHelper;
    private static Context sContext;

    public void onCreate() {
        super.onCreate();
        CustomApplication.sContext = getApplicationContext();
    }

    /**
     * Return the application context.
     *
     * @return application-context
     */
    public static Context getAppContext() {
        return CustomApplication.sContext;
    }

    /**
     * Return the default shared-preferences for this application.
     *
     * @return shared-preferences
     */
    public static SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(getAppContext());
    }

    /**
     * static singleton-method to return the database-helper.
     *
     * @return ORMLite database-helper
     */
    public static DatabaseHelper getDbHelper() {
        if (sDbHelper == null) {
            sDbHelper = new DatabaseHelper(CustomApplication.getAppContext());
        }
        return sDbHelper;
    }
}
