package me.coucou.nutrition;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.sql.SQLException;
import java.util.Calendar;

import me.coucou.nutrition.db.dao.MealsDataSource;
import me.coucou.nutrition.db.model.Meal;

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

        public PlaceholderFragment() {
        }

        public void showTimePickerDialog(View v) {
            DialogFragment newFragment = new DatePickerFragment();
            newFragment.show(getFragmentManager(), "datePicker");
        }

        public void saveMeal(View v) {

            try {
                dataSource = new MealsDataSource(getActivity());
                dataSource.open();
                EditText description = (EditText) getActivity().findViewById(R.id.mealDescriptionEditText);
                Button dateBtn = (Button) getActivity().findViewById(R.id.pickTimeBtn);
                Meal meal = dataSource.createMeal(description.getText().toString(), dateBtn.getText().toString());
                meal.toString();

                startActivity(new Intent(getActivity(), MainActivity.class));

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_create_meal, container, false);
            final Button btn = (Button) rootView.findViewById(R.id.pickTimeBtn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showTimePickerDialog(v);
                }
            });

            final Button save = (Button) rootView.findViewById(R.id.saveMeal);
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveMeal(v);
                }
            });

            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            btn.setText(day + "-" + month + "-" + year);

            return rootView;
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
            Button btn = (Button) getActivity().findViewById(R.id.pickTimeBtn);
            btn.setText(day + "-" + month + "-" + year);
        }
    }

}
