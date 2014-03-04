package me.coucou.nutrition.util;

import android.graphics.Bitmap;
import android.util.Log;

public class BitmapUtils {
    public static String LOG_TAG = "BitmapUtils";
    /**
     * Bitmap width allowed
     */
    public static int BITMAP_SIZE_REQUIRED = 600;

    /**
     * Creates an scaled bitmap according to <code>BITMAP_SIZE_REQUIRED</code>
     *
     * @param source
     * @return scaledBitmap
     */
    public static Bitmap getScaledBitmap(Bitmap source) {
        float w = source.getWidth();
        float h = source.getHeight();
        float scaleX = BITMAP_SIZE_REQUIRED / w;
        float scaleY = scaleX;
        int newWidth = Math.round(w * scaleX);
        int newHeight = Math.round(h * scaleY);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(source, newWidth, newHeight, false);

        Log.d(LOG_TAG, "Original Width:" + w + " Original Height:" + h);
        Log.d(LOG_TAG, "Scaled Widht:" + scaledBitmap.getWidth() + " height:" + scaledBitmap.getHeight());

        return scaledBitmap;
    }
}