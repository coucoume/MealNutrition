package me.coucou.nutrition.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import me.coucou.nutrition.db.dao.MealDBSchema;

/**
 * Created by matias on 2/11/14.
 */
public class NutritionMealSQLiteHelper extends SQLiteOpenHelper {

    //TODO:Data Base creation information need to be moved to a string value
    private static final String DATABASE_NAME = "meals-nutrition.db";
    private static final int DATABASE_VERSION = 3;

    public NutritionMealSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(MealDBSchema.DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(NutritionMealSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + MealDBSchema.TABLE_MEALS);
        onCreate(db);
    }



}
