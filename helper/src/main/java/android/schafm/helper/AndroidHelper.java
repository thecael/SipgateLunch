package android.schafm.helper;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.Settings;

import java.io.File;

/**
 * Android toolkit class.
 *
 * @author schafm
 */
public class AndroidHelper {
    public static final String TAG = AndroidHelper.class.getSimpleName();

    private AndroidHelper() {
    }

    public static String getVersion(Context context) {
        String version = "";
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "unkown";
        }
    }

    public static int getRevision(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            return -1;
        }
    }

    public static String getAppName(Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = packageManager.getApplicationInfo(
                    context.getApplicationInfo().packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {}

        return (String) (applicationInfo != null ? packageManager.getApplicationLabel(
                applicationInfo) : "Unknown");
    }

    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    public static File getIntWorkingDir(Context context) {
            return context.getFilesDir();
    }

    public static boolean isExternalStorageWritable() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    public static File getExtWorkingDir(Context context) {
        if (!isExternalStorageWritable()) return null;
        File workingDir = new File(Environment.getExternalStorageDirectory() +
                File.separator + "data");
        if (!workingDir.exists()) workingDir.mkdir();
        workingDir = new File(workingDir + File.separator + getPackageName(context));
        if (!workingDir.exists()) workingDir.mkdir();
        return workingDir;
    }

    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }
}
