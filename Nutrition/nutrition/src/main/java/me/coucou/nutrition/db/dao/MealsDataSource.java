package me.coucou.nutrition.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import me.coucou.nutrition.db.MealsSQLiteHelper;
import me.coucou.nutrition.db.model.Meal;

/**
 * Created by matias on 2/11/14.
 */
public class MealsDataSource {

    // Database fields
    private SQLiteDatabase database;
    private MealsSQLiteHelper dbHelper;
    private String[] allColumns = {
            MealsSQLiteHelper.COLUMN_ID,
            MealsSQLiteHelper.COLUMN_MEAL,
            MealsSQLiteHelper.COLUMN_DATE,
            MealsSQLiteHelper.COLUMN_TIME};

    public MealsDataSource(Context context) {
        dbHelper = new MealsSQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Meal createMeal(String comment, String date, String time) {
        ContentValues values = new ContentValues();
        values.put(MealsSQLiteHelper.COLUMN_MEAL, comment);
        values.put(MealsSQLiteHelper.COLUMN_DATE, date);
        values.put(MealsSQLiteHelper.COLUMN_TIME, time);

        long insertId = database.insert(MealsSQLiteHelper.TABLE_MEALS, null,
                values);

        Cursor cursor = database.query(MealsSQLiteHelper.TABLE_MEALS,
                allColumns, MealsSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Meal newModel = cursorToComment(cursor);
        cursor.close();
        return newModel;
    }

    public Meal editMeal(String comment, String date, String time,  long id) {
        ContentValues values = new ContentValues();
        values.put(MealsSQLiteHelper.COLUMN_MEAL, comment);
        values.put(MealsSQLiteHelper.COLUMN_DATE, date);
        values.put(MealsSQLiteHelper.COLUMN_TIME, time);
        database.update(MealsSQLiteHelper.TABLE_MEALS, values, MealsSQLiteHelper.COLUMN_ID +" = "+id, null);

        Cursor cursor = database.rawQuery("SELECT * FROM "+MealsSQLiteHelper.TABLE_MEALS+" WHERE "+ MealsSQLiteHelper.COLUMN_ID+" = "+id, null);
        cursor.moveToFirst();
        Meal editedModel = cursorToComment(cursor);
        cursor.close();

        return editedModel;
    }

    public void deleteMeal(Meal comment) {
        long id = comment.getId();
        System.out.println("Meal deleted with id: " + id);
        database.delete(MealsSQLiteHelper.TABLE_MEALS, MealsSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Meal> getAllComments() {
        List<Meal> comments = new ArrayList<Meal>();

        Cursor cursor = database.query(MealsSQLiteHelper.TABLE_MEALS,
                allColumns, null, null, null, null, null);

        //cursor.moveToFirst();
        cursor.moveToLast();

        while (!cursor.isBeforeFirst()) {
            Meal comment = cursorToComment(cursor);
            comments.add(comment);
            cursor.moveToPrevious();
        }
        // make sure to close the cursor
        cursor.close();
        return comments;
    }




    private Meal cursorToComment(Cursor cursor) {
        Meal model = new Meal();
        model.setId(cursor.getLong(0));
        model.setDescription(cursor.getString(1));
        model.setDate(cursor.getString(2));
        model.setTime(cursor.getString(3));
        return model;
    }
}
