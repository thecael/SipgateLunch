package android.schafm.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

/**
 * Helperclass for image manipulation.
 *
 * @author schafm
 */
public class ImageHelper {

    private ImageHelper() {
    }

    /**
     * Rotate a drawable
     * @param b Bitmap
     * @param angle angle
     * @return rotated drawable
     */
    public static Drawable getRotateDrawable(Context context, final Bitmap b, final float angle) {
        final BitmapDrawable drawable = new BitmapDrawable(
                context.getResources(), b) {
            @Override
            public void draw(final Canvas canvas) {
                canvas.save();
                canvas.rotate(angle, b.getWidth() / 2, b.getHeight() / 2);
                super.draw(canvas);
                canvas.restore();
            }
        };
        return drawable;
    }

    /**
     * converts a drawable to image
     * @param drawable drawable
     * @return bitmap
     */
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        int width = drawable.getIntrinsicWidth();
        width = width > 0 ? width : 1;
        int height = drawable.getIntrinsicHeight();
        height = height > 0 ? height : 1;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
