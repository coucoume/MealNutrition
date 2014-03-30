package me.coucou.nutrition.db.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import me.coucou.nutrition.model.Meal;

/**
 * Created by matias on 3/10/14.
 */
public class MealDao extends BaseDao {

    public MealDao(SQLiteOpenHelper dbHelper){
        super(dbHelper);
    }

    public Meal createMeal(String comment, String date, String time, String fullPathImage) {
        ContentValues values = new ContentValues();
        values.put(MealDBSchema.COLUMN_MEAL, comment);
        values.put(MealDBSchema.COLUMN_DATE, date);
        values.put(MealDBSchema.COLUMN_TIME, time);
        values.put(MealDBSchema.COLUMN_IMAGE_PATH, fullPathImage);

        long insertId = getDataBase().insert(MealDBSchema.TABLE_MEALS, null,
                values);

        Cursor cursor = getDataBase().query(MealDBSchema.TABLE_MEALS,
                MealDBSchema.allColumns, MealDBSchema.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Meal newModel = cursorToComment(cursor);
        cursor.close();
        return newModel;
    }

    public Meal editMeal(String comment, String date, String time,  long id) {
        ContentValues values = new ContentValues();
        values.put(MealDBSchema.COLUMN_MEAL, comment);
        values.put(MealDBSchema.COLUMN_DATE, date);
        values.put(MealDBSchema.COLUMN_TIME, time);

        getDataBase().update(MealDBSchema.TABLE_MEALS, values, MealDBSchema.COLUMN_ID + " = " + id, null);

        Cursor cursor = getDataBase().rawQuery("SELECT * FROM " + MealDBSchema.TABLE_MEALS + " WHERE " + MealDBSchema.COLUMN_ID + " = " + id, null);
        cursor.moveToFirst();
        Meal editedModel = cursorToComment(cursor);
        cursor.close();

        return editedModel;
    }

    public void deleteMeal(Meal comment) {
        long id = comment.getId();
        System.out.println("Meal deleted with id: " + id);
        getDataBase().delete(MealDBSchema.TABLE_MEALS, MealDBSchema.COLUMN_ID
                + " = " + id, null);
    }

    public List<Meal> getAllMeals() {
        List<Meal> list = new ArrayList<Meal>();

        Cursor cursor = getDataBase().query(MealDBSchema.TABLE_MEALS,
                MealDBSchema.allColumns, null, null, null, null, null);

        //cursor.moveToFirst();
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()) {
            Meal m = cursorToComment(cursor);
            list.add(m);
            cursor.moveToPrevious();
        }

        // make sure to close the cursor
        cursor.close();

        return list;
    }



    private Meal cursorToComment(Cursor cursor) {
        Meal model = new Meal();

        model.setId(cursor.getLong(0));
        model.setDescription(cursor.getString(1));
        model.setDate(cursor.getString(2));
        model.setTime(cursor.getString(3));
        model.setImagePath(cursor.getString(4));

        return model;
    }
}
