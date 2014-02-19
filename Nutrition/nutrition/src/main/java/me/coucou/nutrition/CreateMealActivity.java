package me.coucou.nutrition;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import me.coucou.nutrition.db.dao.MealsDataSource;
import me.coucou.nutrition.db.model.Meal;
import me.coucou.nutrition.factory.AlbumStorageDirFactory;
import me.coucou.nutrition.factory.BaseAlbumDirFactory;
import me.coucou.nutrition.factory.FroyoAlbumDirFactory;

public class CreateMealActivity extends ActionBarActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.create_meal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private MealsDataSource dataSource;
        private String mCurrentPhotoPath;

        public static int BMP_WIDTH = 300;
        public static int BMP_HEIGHT = 300;

        private AlbumStorageDirFactory mAlbumStorageDirFactory = null;


        private static final String JPEG_FILE_PREFIX = "IMG_";
        private static final String JPEG_FILE_SUFFIX = ".jpg";
        private static final int ACTION_CAMERA = 1;

        public PlaceholderFragment() {
        }

        public void showTimeDatePickerDialog(View v) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }

        public void showTimeHourPickerDialog(View v) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(), "timePicker");
        }

        public void saveMeal(View v) {

            try {
                dataSource = new MealsDataSource(getActivity());
                dataSource.open();
                EditText description = (EditText) getActivity().findViewById(R.id.mealDescriptionEditText);
                Button dateBtn = (Button) getActivity().findViewById(R.id.pickTimeDateBtn);
                Button timeBtn = (Button) getActivity().findViewById(R.id.pickTimeHourBtn);
                Meal model = dataSource.createMeal(
                        description.getText().toString(),
                        dateBtn.getText().toString(),
                        timeBtn.getText().toString());
                Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();

            } catch (SQLException e) {
                //TODO: Handle exceptions
                e.printStackTrace();
            }
        }

        public void editMeal(View v, long id) {

            try {
                dataSource = new MealsDataSource(getActivity());
                dataSource.open();
                EditText description = (EditText) getActivity().findViewById(R.id.mealDescriptionEditText);
                Button dateBtn = (Button) getActivity().findViewById(R.id.pickTimeDateBtn);
                Button timeBtn = (Button) getActivity().findViewById(R.id.pickTimeHourBtn);
                Meal model = dataSource.editMeal(
                        description.getText().toString(),
                        dateBtn.getText().toString(),
                        timeBtn.getText().toString(),
                        id);
                Toast.makeText(getActivity(), model.toString(), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getActivity(), MainActivity.class));

                //Remove the meal
                ((NutritionApplication)getActivity().getApplication()).currentMeal = null;

                getActivity().finish();

            } catch (SQLException e) {
                //TODO: Handle exceptions
                e.printStackTrace();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_meal, container, false);


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
                mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
            } else {
                mAlbumStorageDirFactory = new BaseAlbumDirFactory();
            }

            final Button btnDate = (Button) rootView.findViewById(R.id.pickTimeDateBtn);
            btnDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeDatePickerDialog(v);
                }
            });

            final Button btnTime = (Button) rootView.findViewById(R.id.pickTimeHourBtn);
            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimeHourPickerDialog(v);
                }
            });


            final Button saveEdit = (Button) rootView.findViewById(R.id.saveMeal);

            NutritionApplication app = (NutritionApplication) getActivity().getApplication();
            final Meal model = app.currentMeal;

            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            //Means that is editing
            if(model != null){
                btnDate.setText(model.getDate());
                btnTime.setText(model.getTime());
                EditText txtEdit = (EditText) rootView.findViewById(R.id.mealDescriptionEditText);
                txtEdit.setText(model.getDescription());
                saveEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editMeal(v, model.getId());
                    }
                });

            } else {
                saveEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        saveMeal(v);
                    }
                });
                btnDate.setText(day + "-" + month + "-" + year);
                btnTime.setText(hour + ":" + minute);

                //New mode-> let you add an image
                ImageView img = (ImageView) rootView.findViewById(R.id.mealImage);
                img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        captureImage();
                    }
                });

            }
            return rootView;
        }

        //takes a picture of the meal
        private void captureImage(){
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
            if (resultCode == Activity.RESULT_OK) {
                Log.d(getActivity().toString(), "OK picture taken");

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
                Bitmap bmp = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
                ImageView img = (ImageView) getActivity().findViewById(R.id.mealImage);
                img.setImageBitmap(bmp);

                Log.d(this.toString(), "image meal:\n"+bmp.toString());

            } else {
                //TODO: Implement code for cancelled operation
                Toast.makeText(getActivity(),"Operation cancelled", Toast.LENGTH_SHORT).show();
            }
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

        @Override
        public void onResume(){
            try {
                if(dataSource != null){
                    dataSource.open();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            super.onResume();
        }

        @Override
        public void onPause() {
            if(dataSource != null){
                dataSource.close();
            }
            super.onPause();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month += 1;
            Button btn = (Button) getActivity().findViewById(R.id.pickTimeDateBtn);
            btn.setText(day + "-" + month + "-" + year);
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // Do something with the date chosen by the user
            Button btn = (Button) getActivity().findViewById(R.id.pickTimeHourBtn);
            btn.setText(hourOfDay + ":" + minute);
        }
    }

}
