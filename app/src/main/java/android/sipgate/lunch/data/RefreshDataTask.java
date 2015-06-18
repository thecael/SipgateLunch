package android.sipgate.lunch.data;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.sipgate.lunch.exception.MenuParsingException;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import android.sipgate.lunch.CustomApplication;
import android.sipgate.lunch.ormlite.Meal;
import android.widget.Toast;

/**
 * Async task to refresh new menu data in the background.
 * Sends a broadcast when the process is completed.
 *
 * @author schafm
 */
public class RefreshDataTask extends AsyncTask<Void, Void, Boolean> {
    private static final String TAG = RefreshDataTask.class.getSimpleName();
    public static final int RESPONSE_REFRESH_FINISHED = 101;
    public static final int RESPONSE_REFRESH_ERROR = 102;

    private Context mContext;
    private String mAction;
    private String mErrorMsg = "";

    public RefreshDataTask(Context context, String action) {
        mContext = context;
        mAction = action;
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            List<Meal> mealsList = new MenuParser().parseMenuSite();
            if (mealsList == null) {
                return false;
            }

            CustomApplication.getDbHelper().clearMealTable();
            for (Meal meal : mealsList) {
                CustomApplication.getDbHelper().getMealDao().create(meal);
            }
        } catch (SQLException e) {
            Log.e(TAG, "Error trying so save a meal in database!", e);
            mErrorMsg = "Menü konnte nicht gespeichert werden!";
            return false;
        } catch (MenuParsingException | ParseException e) {
            Log.e(TAG, "Error trying to parse the menu!", e);
            mErrorMsg = "Menü konnte nicht gelesen werden!";
            return false;
        } catch (IOException e) {
            Log.w(TAG, "cannot access menu webpage!", e);
            mErrorMsg = "Altepost nicht erreichbar!";
            return false;
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        Intent intent = new Intent(mAction);
        if (result) {
            intent.putExtra("result", RESPONSE_REFRESH_FINISHED);
        } else {
            intent.putExtra("result", RESPONSE_REFRESH_ERROR);
            intent.putExtra("error_msg", mErrorMsg);
        }
        mContext.sendBroadcast(intent);
    }
}