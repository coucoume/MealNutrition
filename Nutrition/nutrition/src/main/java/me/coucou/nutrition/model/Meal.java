package me.coucou.nutrition.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;

import me.coucou.nutrition.R;
import me.coucou.nutrition.util.MealCalendar;

/**
 * Created by matias on 2/11/14.
 */
public class Meal{
    public static long DEFAULT_ID         = -1;
    public static int BMP_WIDTH           = 400;
    public static int BMP_HEIGHT          = 400;
    public static String JPEG_FILE_PREFIX = "IMG_";
    public static String JPEG_FILE_SUFFIX = ".jpg";

    private Context mContext;

    private long id = DEFAULT_ID;
    private String description;
    private String date;
    private String time;
    private String imagePath;



    public Meal(Context context){
        mContext = context;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        //Return default
        if(description == null){
            return mContext.getString(R.string.default_meal_description);
        }

        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        //return default
        if(date == null){
            return MealCalendar.getCalendarDate();
        }

        return date;
    }

    /**
     *
     * @return image bitmap
     */
    public Bitmap getBitmap(){
        Bitmap bmp;
        String picUrl = getImagePath();

        if(picUrl != null){
            File imgFile = new  File(picUrl);
            if(imgFile.exists()){
                bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            } else {
                Log.d("Meal Model", "Image file is missing in the scard, so not showing");
                bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.meal_placeholder);
            }
        } else {
            Log.d("Meal Model", "Image file is empty in the registry, so not showing");
            bmp = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.meal_placeholder);
        }

        return bmp;
    }

    public Bitmap buildBitmap(){
        Bitmap pictureBmp;
        /* Get the size of the image */
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(getImagePath(), bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		    /* get the minimum scale */
        int scaleFactor = Math.min(photoW / BMP_WIDTH, photoH / BMP_HEIGHT);

		    /* Set bitmap options to scale the image decode target */
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

		    /* Re sample the JPEG file into a Bitmap */
        pictureBmp = BitmapFactory.decodeFile(getImagePath(), bmOptions);

        return pictureBmp;
    }

    public String getTime() {
        // return default
        if(time == null){
            return MealCalendar.getCalendarTime();
        }
        return time;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String value) {
        this.imagePath = value;
    }

    public String getFullDateTime(){
        return date +" "+time;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return date+" - "+ time +"\n" +description +"\n"+imagePath;
    }

}
