package me.coucou.nutrition.db.dao;

/**
 * Created by matias on 3/10/14.
 */
public class MealDBSchema {

    //table
    public static final String TABLE_MEALS = "meals";
    //fields
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MEAL= "meal";
    public static final String COLUMN_DATE= "date";
    public static final String COLUMN_TIME= "time";
    public static final String COLUMN_IMAGE_PATH= "imagePath";

    //all columns
    public static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_MEAL,
            COLUMN_DATE,
            COLUMN_TIME,
            COLUMN_IMAGE_PATH
    };

    // Database creation sql statement
    public static final String DATABASE_CREATE = "create table "
            + TABLE_MEALS + "("
            + COLUMN_ID   + " integer primary key autoincrement, "
            + COLUMN_MEAL + " text not null, "
            + COLUMN_DATE + " text not null, "
            + COLUMN_TIME + " text not null, "
            + COLUMN_IMAGE_PATH + " text not null);";
}
