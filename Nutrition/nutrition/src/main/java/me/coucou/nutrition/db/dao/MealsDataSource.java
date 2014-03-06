package me.coucou.nutrition.db.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import me.coucou.nutrition.db.MealsSQLiteHelper;
import me.coucou.nutrition.db.model.Meal;
import me.coucou.nutrition.db.model.Tag;

/**
 * Created by matias on 2/11/14.
 */
public class MealsDataSource {

    //Hash to handle meal TAGS
    private HashMap<String, Tag> mTags = new HashMap<String, Tag>();
    private HashMap<String, Tag> mListedTags = new HashMap<String, Tag>();

    // Database fields
    private SQLiteDatabase database;
    private MealsSQLiteHelper dbHelper;
    private String[] allColumns = {
            MealsSQLiteHelper.COLUMN_ID,
            MealsSQLiteHelper.COLUMN_MEAL,
            MealsSQLiteHelper.COLUMN_DATE,
            MealsSQLiteHelper.COLUMN_TIME,
            MealsSQLiteHelper.COLUMN_IMAGE_PATH};

    public MealsDataSource(Context context) {
        dbHelper = new MealsSQLiteHelper(context);

        mTags.put("papa", new Tag("papa", 1, 10));
        mTags.put("carne", new Tag("carne", 2, 20));
        mTags.put("pescado", new Tag("pescado", 3, 30));
        mTags.put("pollo", new Tag("pollo", 4, 40));
        mTags.put("lechuga", new Tag("lechuga", 5, 50));

    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Meal createMeal(String comment, String date, String time, String fullPathImage) {
        ContentValues values = new ContentValues();
        values.put(MealsSQLiteHelper.COLUMN_MEAL, comment);
        values.put(MealsSQLiteHelper.COLUMN_DATE, date);
        values.put(MealsSQLiteHelper.COLUMN_TIME, time);
        values.put(MealsSQLiteHelper.COLUMN_IMAGE_PATH, fullPathImage);

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

    public Tag getTagByLabel(String label){
        boolean found = mTags.containsKey(label);
        if(found){
            return mTags.get(label);
        }
        return null;

    }

    public boolean tagToList(Tag tag){
        boolean result = mListedTags.containsValue(tag);

        if(result == false){
            mListedTags.put(tag.getLabel(), tag);
            return true;
        }

        return false;
    }

    public void printListedTags(){

        Iterator<String> keySetIterator = mListedTags.keySet().iterator();

        while(keySetIterator.hasNext()){
            String key = keySetIterator.next();
            //TODO:Add rutine to add buttons to the labels
            Log.d(this.toString(), "key in tag list:"+ key);
        }
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
