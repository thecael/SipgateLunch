package android.sipgate.lunch.ormlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.io.File;
import java.sql.SQLException;

import android.sipgate.lunch.CustomApplication;
import android.sipgate.lunch.sipgatelunch.R;

import android.schafm.helper.AndroidHelper;

/**
 * A database helper class with DAO objects.
 *
 * @author schafm
 * @see OrmLiteSqliteOpenHelper
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    public static final String TAG = DatabaseHelper.class.getSimpleName();
    public static final String DATABASE_NAME = "lunch_v1.db";
    private static final int DATABASE_VERSION = 1;

    private Dao<Meal, Integer> mMealDao = null;

    public DatabaseHelper(Context context) {
        super(context, getDbPathAsStr(context), null, DATABASE_VERSION, R.raw.ormlite_config);
    }

    /**
     * Return the database path as a string.
     *
     * @param context Context
     * @return database path as a string
     */
    private static String getDbPathAsStr(Context context) {
        String dbPath;
        if (CustomApplication.TEST_MODE)
            dbPath = AndroidHelper.getExtWorkingDir(context) + File.separator + DATABASE_NAME;
        else
            dbPath = AndroidHelper.getIntWorkingDir(context) + File.separator + DATABASE_NAME;
        Log.d(TAG, "Using Database file: " + dbPath);
        return dbPath;
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        createDatabase();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
    }

    private void createDatabase() {
        try {
            Log.d(TAG, "checking database structure ...");

            TableUtils.createTableIfNotExists(connectionSource, Meal.class);
            Log.d(TAG, "creating table '" + Meal.TABLE_NAME + "'");
        } catch (SQLException e) {
            Log.e(TAG, "error trying to create a database table!", e);
        }
    }

    public void clearMealTable() throws SQLException {
        TableUtils.clearTable(connectionSource, Meal.class);
    }

    public void resetDatabase() {
        Log.d(TAG, "resetting database ...");

        try {
            TableUtils.dropTable(connectionSource, Meal.class, true);
            Log.d(TAG, "deleting table '" + Meal.TABLE_NAME + "'");
            createDatabase();
        } catch (SQLException e) {
            Log.e(TAG, "error trying to reset database!", e);
        }
    }

    public Dao<Meal, Integer> getMealDao() throws SQLException {
        if (mMealDao == null) {
            mMealDao = DaoManager.createDao(getConnectionSource(), Meal.class);
        }
        return mMealDao;
    }
}
