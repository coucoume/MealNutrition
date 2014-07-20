package me.coucou.nutrition.ui;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import me.coucou.nutrition.MainActivity;
import me.coucou.nutrition.NutritionApplication;
import me.coucou.nutrition.R;
import me.coucou.nutrition.db.DBManager;
import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.db.dao.TagDao;
import me.coucou.nutrition.factory.AlbumStorageDirFactory;
import me.coucou.nutrition.factory.BaseAlbumDirFactory;
import me.coucou.nutrition.factory.FroyoAlbumDirFactory;
import me.coucou.nutrition.model.Meal;
import me.coucou.nutrition.util.MealCalendar;

/**
 * Created by matias on 6/10/14.
 */
public class CreateMealFragment extends Fragment{

    private static String TAG = "CreateMealFragment";

    //Application reference
    private NutritionApplication mNutritionApplication;

    //UI Controls
    private View mCurrentView;
    private ImageView mImageView;
    private EditText mDescription;
    private Button mPickTimeDateBtn;
    private Button mPickTimeHourBtn;
    private Button mSaveMeal;

    //DB
    private Meal mModel;
    private MealDao mealDao;
    private TagDao tagDao;

    private static final int ACTION_CAMERA = 1;

    private AlbumStorageDirFactory mAlbumStorageDirFactory = null;

    public static CreateMealFragment newInstance(Long modelId){
        CreateMealFragment f = new CreateMealFragment();
        Bundle args = new Bundle();
        args.putLong("model_id", modelId);
        f.setArguments(args);

        return f;
    }

    public CreateMealFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {

            mNutritionApplication = (NutritionApplication) getActivity().getApplication();

            mealDao = mNutritionApplication.getMealDao();
            //TODO refactor getTagDao as mealDao
            tagDao  = DBManager.getInstance().getTagDao();
            DBManager.getInstance().open();

            //check if there's a saved state in order to retrieve saved data and populate UI controls
            if (savedInstanceState != null){
                mModel = mNutritionApplication.currentMeal;
            }else{
                mModel = mealDao.getMealById(getArguments().getLong("model_id", Meal.DEFAULT_ID));
                mNutritionApplication.currentMeal = mModel;
            }


        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Default UI controls and saved states
        mCurrentView        = inflater.inflate(R.layout.fragment_create_meal, container, false);
        mImageView          = (ImageView) mCurrentView.findViewById(R.id.mealImage);
        mDescription        = (EditText) mCurrentView.findViewById(R.id.mealDescriptionEditText);
        mPickTimeDateBtn    = (Button) mCurrentView.findViewById(R.id.pickTimeDateBtn);
        mPickTimeHourBtn    = (Button) mCurrentView.findViewById(R.id.pickTimeHourBtn);
        mSaveMeal           = (Button) mCurrentView.findViewById(R.id.saveMeal);

        createUIPickers();
        createUIEditItemStrategy();

        return mCurrentView;
    }

    /**
     * Binds components in order to edit a model
     */
    private void createUIEditItemStrategy(){

        //edit meal button
        ((Button) mCurrentView.findViewById(R.id.saveMeal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeal();
            }
        });

        mDescription.setText(mModel.getDescription());
        mImageView.setImageBitmap(mModel.getBitmap());
        mPickTimeDateBtn.setText(mModel.getDate());
        mPickTimeHourBtn.setText(mModel.getTime());

        //New mode-> let you add an image
        ((ImageButton) mCurrentView.findViewById(R.id.imageButtonCamera)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Add an Interface
                captureImage();
            }
        });

        // Add text watcher in order to check user input and read all tags related
        mDescription.addTextChangedListener(new TextMealWatcher(getActivity()));
    }

    /**
     * Binds all components with their actions
     */
    private void createUIPickers(){
        //Date control
        final Button btnDate = mPickTimeDateBtn;
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDatePickerDialog(v);
            }
        });

        //Time control
        final Button btnTime = mPickTimeHourBtn;
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeHourPickerDialog(v);
            }
        });

        btnDate.setText(mModel.getDate());
        btnTime.setText(mModel.getTime());
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Update current UI information to model
        mModel.setDescription(mDescription.getText().toString());
        mModel.setDate(mPickTimeDateBtn.getText().toString());
        mModel.setTime(mPickTimeHourBtn.getText().toString());

        super.onSaveInstanceState(outState);
    }

    private void saveMeal(){

        mModel.setDescription(mDescription.getText().toString());
        mModel.setDate(mPickTimeDateBtn.getText().toString());
        mModel.setTime(mPickTimeHourBtn.getText().toString());

        if(mModel.getId() == Meal.DEFAULT_ID){
            Meal savedModel = mealDao.createMeal(mModel.getDescription(), mModel.getDate(),mModel.getTime(),mModel.getImagePath());
        }else{
            Meal editedModel = mealDao.editMeal(mModel.getDescription(), mModel.getDate(),mModel.getTime(),mModel.getId(), mModel.getImagePath());
        }
        /*
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "mealThumb"+ timeStamp + ".jpg";
        File albumF = getAlbumDir();

        try{
            String fullFileName = albumF.getAbsolutePath()+ File.separatorChar + imageFileName;
            Log.d(this.toString(), "full file name:"+fullFileName);

            FileOutputStream thumbFileOS = new FileOutputStream(fullFileName);
            BufferedOutputStream bos = new BufferedOutputStream(thumbFileOS);
            mPhoto.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            bos.flush();
            bos.close();

            Meal savedModel = mealDao.createMeal(mModel.getDescription(), mModel.getDate(),mModel.getTime(),mModel.getImagePath());

            Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.w(this.toString(), "Error saving image file: " + e.getMessage());

        } catch (IOException e) {
            Log.w(this.toString(), "Error saving image file: " + e.getMessage());

        }

        */

         /*String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "mealThumb"+ timeStamp + ".jpg";
        File albumF = getAlbumDir();
        try{
            String fullFileName = albumF.getAbsolutePath()+ File.separatorChar+imageFileName;
            Log.d(this.toString(), "full file name:"+fullFileName);

            FileOutputStream thumbFileOS = new FileOutputStream(fullFileName);
            BufferedOutputStream bos = new BufferedOutputStream(thumbFileOS);
            mPhoto.compress(Bitmap.CompressFormat.JPEG, 80, bos);

            bos.flush();
            bos.close();


            Meal model = mealDao.createMeal(
                    description.getText().toString(),
                    dateBtn.getText().toString(),
                    timeBtn.getText().toString(),
                    fullFileName);
            Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();

        } catch (FileNotFoundException e) {
            Log.w(this.toString(), "Error saving image file: " + e.getMessage());

        } catch (IOException e) {
            Log.w(this.toString(), "Error saving image file: " + e.getMessage());

        }
        */
        /*Meal model = mealDao.createMeal(
                description.getText().toString(),
                dateBtn.getText().toString(),
                timeBtn.getText().toString(),
                app.currentPathToFile);
        */
        Toast.makeText(getActivity(), mModel.toString(), Toast.LENGTH_SHORT).show();

        //destroy current references
        mNutritionApplication.currentMeal = null;
        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();

    }

    public void captureImage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
            mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
        } else {
            mAlbumStorageDirFactory = new BaseAlbumDirFactory();
        }
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File f = null;
        try {
            f = createImageFile();
            mModel.setImagePath(f.getAbsolutePath());
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        } catch (IOException e) {
            e.printStackTrace();
            mModel.setImagePath(null);
        }

        startActivityForResult(takePictureIntent, ACTION_CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {

            Log.d(TAG, "OK picture taken");

            Bitmap bmp = mModel.buildBitmap();
            if(bmp != null){
                mImageView.setImageBitmap(mModel.buildBitmap());
            } else{
                Log.e(TAG, "Null bmp received from TakePhotoFragment");
            }

        } else {
           deleteImageFile();
        }
    }

    private void deleteImageFile(){
        //TODO: Implement code for cancelled operation
        File file = new File(mModel.getImagePath());
        boolean deleted = file.delete();
        Toast.makeText(getActivity(), "Operation cancelled:" + deleted, Toast.LENGTH_SHORT).show();
    }

    /**
     * Create a Bitmap file
     *
     * @return File
     * @throws java.io.IOException
     */
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = Meal.JPEG_FILE_PREFIX + timeStamp + "_";
        File albumF = getAlbumDir();
        File imageF = File.createTempFile(imageFileName, Meal.JPEG_FILE_SUFFIX, albumF);

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


    public void showTimeDatePickerDialog(View v) {
        DialogFragment newFragment = new MealCalendar.DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimeHourPickerDialog(View v) {
        DialogFragment newFragment = new MealCalendar.TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    @Override
    public void onResume(){
        try {
            DBManager.getInstance().open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        DBManager.getInstance().close();
        super.onPause();
    }


}
