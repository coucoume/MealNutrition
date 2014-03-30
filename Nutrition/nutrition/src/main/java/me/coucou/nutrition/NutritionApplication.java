package me.coucou.nutrition;

import android.app.Application;

import me.coucou.nutrition.db.DBManager;
import me.coucou.nutrition.db.NutritionMealSQLiteHelper;
import me.coucou.nutrition.model.Meal;

/**
 * Created by matias on 2/12/14.
 */
public class NutritionApplication extends Application {
    //TODO: Remove this in order to support all meals and DB connection in the application scope
    public Meal currentMeal;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeDBManager();
    }

    private void initializeDBManager(){
        DBManager.getInstance().init(
                new NutritionMealSQLiteHelper(getApplicationContext())
        );
    }


}
