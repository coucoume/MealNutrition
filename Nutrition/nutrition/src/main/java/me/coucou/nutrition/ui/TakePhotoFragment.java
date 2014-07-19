package me.coucou.nutrition.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.coucou.nutrition.R;
import me.coucou.nutrition.factory.AlbumStorageDirFactory;
import me.coucou.nutrition.factory.BaseAlbumDirFactory;
import me.coucou.nutrition.factory.FroyoAlbumDirFactory;

/**
 * Created by matias on 6/12/14.
 */
public class TakePhotoFragment extends Fragment{

    private static String TAG = "TakePhotoFragment";
    public static int BMP_WIDTH = 300;
    public static int BMP_HEIGHT = 300;
    private static final String JPEG_FILE_PREFIX = "IMG_";
    private static final String JPEG_FILE_SUFFIX = ".jpg";
    private static final int ACTION_CAMERA = 1;

    private String mCurrentPhotoPath;
    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


    public TakePhotoFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        captureImage();
    }

    /*
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //No view attached
        return null;
    }*/

    //takes a picture of the meal
    private void captureImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;

        try {
            f = createImageFile();
            mCurrentPhotoPath = f.getAbsolutePath();
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();

            f = null;
            mCurrentPhotoPath = null;
        }

        startActivityForResult(takePictureIntent, ACTION_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap pictureBmp = null;
        if (resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "OK picture taken");

            		/* Get the size of the image */
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

		    /* get the minimum scale */
            int scaleFactor = Math.min(photoW / BMP_WIDTH, photoH / BMP_HEIGHT);

		    /* Set bitmap options to scale the image decode target */
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

		    /* Re sample the JPEG file into a Bitmap */
            pictureBmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);


            /*final ImageView img = (ImageView) getActivity().findViewById(R.id.mealImage);
            img.setImageBitmap(bmp);

            ImageButton imgBtn = (ImageButton)getActivity().findViewById(R.id.imageButtonRotate);
            imgBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Canvas c = new Canvas();
                    Matrix m = new Matrix();
                    m.postRotate(-90.0f);
                    Bitmap b = Bitmap.createBitmap(bmp,0,0,bmp.getWidth(),bmp.getHeight(),m,false);
                    img.draw(c);
                }
            });
            */

        } else {
            //TODO: Implement code for cancelled operation
            File file = new File(mCurrentPhotoPath);
            boolean deleted = file.delete();
            Toast.makeText(getActivity(), "Operation cancelled:" + deleted, Toast.LENGTH_SHORT).show();
        }

        ((TakePhotoInterface) getActivity()).onPhotoTaken(pictureBmp, mCurrentPhotoPath);
    }

    /**
     * Create a Bitmap file
     *
     * @return File
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);

        return imageF;
    }

    private File getAlbumDir() {
        File storageDir = null;

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getString(R.string.album_name));
            if (storageDir != null) {
                if (!storageDir.mkdirs()) {
                    if (!storageDir.exists()) {
                        Log.d("CameraSample", "failed to create directory");
                        return null;
                    }
                }
            }

        } else {
            Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
        }

        return storageDir;
    }


}
