package me.coucou.nutrition;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import me.coucou.nutrition.ui.CreateMealFragment;
import me.coucou.nutrition.ui.TakePhotoFragment;
import me.coucou.nutrition.ui.TakePhotoInterface;

/**
 * Creates a Meal
 * @author mailmatiasmunoz@gmail.com
 *
 */
public class CreateMealActivity extends ActionBarActivity implements TakePhotoInterface{

    private static String TAG = "CreateMealActivity";

    private FragmentManager mFragmentManager;

    //Fragment that holds all logic related to meal
    private CreateMealFragment mCreateMealFragment;
    //Fragment that takes pictures
    private TakePhotoFragment mTakePhotoFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meal);

        mFragmentManager = getSupportFragmentManager();
        mCreateMealFragment = (CreateMealFragment) mFragmentManager.findFragmentByTag("CreateMealFragment");
        if ( mCreateMealFragment == null) {
            mCreateMealFragment = new CreateMealFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.container, mCreateMealFragment, "CreateMealFragment")
                    .commit();
        }
    }

    @Override
    public void takePhoto(){
        mFragmentManager = getSupportFragmentManager();
        mTakePhotoFragment = (TakePhotoFragment) mFragmentManager.findFragmentByTag("TakePhotoFragment");
        if ( mTakePhotoFragment == null) {
            mTakePhotoFragment = new TakePhotoFragment();
            mFragmentManager.beginTransaction()
                    .add(R.id.container, mTakePhotoFragment, "TakePhotoFragment")
                    .commit();
        }

    }

    @Override
    public void onPhotoTaken(Bitmap bmp, String path){
        if(bmp != null){
            mCreateMealFragment.setPhoto(bmp, path);
            FragmentTransaction ft= mFragmentManager.beginTransaction();
            ft.remove(mTakePhotoFragment);
        } else {
            Log.d(TAG, "Null bmp received from TakePhotoFragment");
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
