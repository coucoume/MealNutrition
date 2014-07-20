package me.coucou.nutrition;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.ui.CreateMealFragment;

/**
 * Creates a Meal
 * @author mailmatiasmunoz@gmail.com
 *
 */
public class CreateMealActivity extends ActionBarActivity{

    private static String TAG = "CreateMealActivity";

    private FragmentManager mFragmentManager;

    //Fragment that holds all logic related to meal
    private CreateMealFragment mCreateMealFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);
        mFragmentManager    = getSupportFragmentManager();
        mCreateMealFragment = (CreateMealFragment) mFragmentManager.findFragmentByTag("CreateMealFragment");
        if(mCreateMealFragment == null){
            Long modelId = getIntent().getLongExtra("model_id", MealDao.DEFAULT_ID_VALUE);

            mCreateMealFragment = CreateMealFragment.newInstance(modelId);
            mFragmentManager.beginTransaction()
                .add(R.id.container, mCreateMealFragment, "CreateMealFragment")
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
}
