package me.coucou.nutrition.ui;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.sql.SQLException;

import me.coucou.nutrition.MainActivity;
import me.coucou.nutrition.NutritionApplication;
import me.coucou.nutrition.R;
import me.coucou.nutrition.db.DBManager;
import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.db.dao.TagDao;
import me.coucou.nutrition.model.Meal;
import me.coucou.nutrition.util.MealCalendar;

/**
 * Created by matias on 6/10/14.
 */
public class CreateMealFragment extends Fragment{

    private static String TAG = "CreateMealFragment";

    private ImageView mImageView;
    private MealDao mealDao;
    private TagDao tagDao;
    private String mImagePath;

    private Bitmap mPhoto;

    /**
     * Constructor
     */
    public CreateMealFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        try {
            mealDao = DBManager.getInstance().getMealDao();
            tagDao  = DBManager.getInstance().getTagDao();
            DBManager.getInstance().open();
        } catch (SQLException e) {
            Log.e(TAG, e.toString());
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_meal, container, false);
        //checks if is editing or adding a new Meal
        Meal model = ((NutritionApplication) getActivity().getApplication()).currentMeal;

        createUIPickers(rootView, model);
        createUINewItemEditItem(rootView, model);

        return rootView;
    }

    /**
     * Create and setup UI elements to display image and description
     * @param rootView
     * @param model
     */
    private void createUINewItemEditItem(View rootView, Meal model){
        if(model != null){
            //current model set from the main list, means that is editing
            createUIEditItemStrategy(model, rootView);
        } else {
            //model is null, means that is creating a new Meal
            createUINewItemStrategy(rootView);
        }
    }

    /**
     * Binds components in order to edit a model
     *
     * @param model
     */
    private void createUIEditItemStrategy(final Meal model, View rootView){
        //edit meal button
        ((Button) rootView.findViewById(R.id.saveMeal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMeal(v, model);
            }
        });

        EditText txtEdit = (EditText) rootView.findViewById(R.id.mealDescriptionEditText);
        txtEdit.setText(model.getDescription());

        //Retrieves image from model and loads from its path
        String picUrl = model.getImagePath();
        if(picUrl != null){
            File imgFile = new  File(picUrl);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ImageView myImage = (ImageView) rootView.findViewById(R.id.mealImage);
                myImage.setImageBitmap(myBitmap);
            } else {
                Log.d(TAG, "Image file is missing in the scard, so not showing");
            }
        } else {
            Log.d(TAG, "Image file is empty in the registry, so not showing");
        }
    }
    /**
     * Binds component in order to create a new model
     *
     * @param rootView
     */
    private void createUINewItemStrategy(View rootView){
        //edit meal button
        ((Button) rootView.findViewById(R.id.saveMeal)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMeal(v);
            }
        });

        //New mode-> let you add an image
        ((ImageView) rootView.findViewById(R.id.mealImage)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TakePhotoInterface) getActivity()).takePhoto();
            }
        });

        // Add text watcher in order to check user input and read all tags related
        EditText description = (EditText) rootView.findViewById(R.id.mealDescriptionEditText);
        description.addTextChangedListener(new TextMealWatcher(getActivity()));

    }
    /**
     * Binds all components with their actions
     *
     * @param rootView
     */
    private void createUIPickers(View rootView, Meal model){
        //Date control
        final Button btnDate = (Button) rootView.findViewById(R.id.pickTimeDateBtn);
        btnDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDatePickerDialog(v);
            }
        });

        //Time control
        final Button btnTime = (Button) rootView.findViewById(R.id.pickTimeHourBtn);
        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeHourPickerDialog(v);
            }
        });

        if(model != null){
            btnDate.setText(model.getDate());
            btnTime.setText(model.getTime());
        } else {
            btnTime.setText(MealCalendar.getCalendarTime());
            btnDate.setText(MealCalendar.getCalendarDate());
        }

    }

    private void saveMeal(View v){

        EditText description = (EditText) getActivity().findViewById(R.id.mealDescriptionEditText);
        Button dateBtn = (Button) getActivity().findViewById(R.id.pickTimeDateBtn);
        Button timeBtn = (Button) getActivity().findViewById(R.id.pickTimeHourBtn);


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

        Meal model = mealDao.createMeal(
                description.getText().toString(),
                dateBtn.getText().toString(),
                timeBtn.getText().toString(),
                mImagePath);
        Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();

        mPhoto = null;

        startActivity(new Intent(getActivity(), MainActivity.class));
        getActivity().finish();

    }

    private void editMeal(View v, Meal model) {
        long id = model.getId();

        EditText description = (EditText) getActivity().findViewById(R.id.mealDescriptionEditText);
        Button dateBtn = (Button) getActivity().findViewById(R.id.pickTimeDateBtn);
        Button timeBtn = (Button) getActivity().findViewById(R.id.pickTimeHourBtn);

        Meal editedModel = mealDao.editMeal(
                description.getText().toString(),
                dateBtn.getText().toString(),
                timeBtn.getText().toString(),
                id,
                mImagePath);

        Toast.makeText(getActivity(), editedModel.toString(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(getActivity(), MainActivity.class));

        //Remove the meal
        ((NutritionApplication)getActivity().getApplication()).currentMeal = null;

        getActivity().finish();
    }

    public void setPhoto(Bitmap bmp, String path){
        mPhoto = bmp;
        mImagePath = path;
        mImageView = (ImageView) getActivity().findViewById(R.id.mealImage);
        mImageView.setImageBitmap(mPhoto);
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
