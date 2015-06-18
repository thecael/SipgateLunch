package android.schafm.helper;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;

/**
 * Some UI helper methods.
 * @author schafm
 */
public class UiHelper {

    private UiHelper() {
    }

    /**
     * get display width of the device.
     * @return display width
     */
    public static int getDisplayWidth() {
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    /**
     * get display height of the device.
     * @return display height
     */
    public static int getDisplayHeight() {
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    /**
     * convert dp to px.
     * @param dp dp
     * @return px
     */
    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * convert px to dp.
     * @param px px
     * @return dp
     */
    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    /**
     * Calculates the Action Bar height in pixels.
     */
    public static int calculateActionBarSize(Context context, int[] resIdsActionBarSize) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(resIdsActionBarSize);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }
}
