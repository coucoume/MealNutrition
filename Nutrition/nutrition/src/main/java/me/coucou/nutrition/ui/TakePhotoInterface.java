package me.coucou.nutrition.ui;

import android.graphics.Bitmap;

/**
 * Created by matias on 6/12/14.
 */
public interface TakePhotoInterface {
    public void takePhoto();
    public void onPhotoTaken(Bitmap bmp, String path);
}
