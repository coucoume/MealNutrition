package me.coucou.nutrition;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import me.coucou.nutrition.adapter.MealListAdapter;
import me.coucou.nutrition.db.DBManager;
import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.model.Meal;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
        private MealDao mealDao;

        public PlaceholderFragment() {}

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            try {
                mealDao = DBManager.getInstance().getMealDao();
                DBManager.getInstance().open();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            final ListView list = (ListView) rootView.findViewById(R.id.mealList);
            TextView noMeal = (TextView) rootView.findViewById(R.id.txtNoMeal);

            ImageButton addBtn = (ImageButton) rootView.findViewById(R.id.addMeal);
            addBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createMealIntent = new Intent(getActivity(), CreateMealActivity.class);
                    startActivity(createMealIntent);
                }
            });

            List<Meal> values = mealDao.getAllMeals();

            //SHOW the textfield that display create your first meal
            if(values.isEmpty()){
                noMeal.setVisibility(View.VISIBLE);
            }

            MealListAdapter adapter = new MealListAdapter(getActivity(), values);
            list.setAdapter(adapter);

            list.setClickable(true);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Meal model = (Meal) list.getAdapter().getItem(position);

                    Intent createMealIntent = new Intent(getActivity(), CreateMealActivity.class);
                    NutritionApplication app = (NutritionApplication) getActivity().getApplication();
                    app.currentMeal = model;
                    startActivity(createMealIntent);
                }
            });


            return rootView;
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

}
