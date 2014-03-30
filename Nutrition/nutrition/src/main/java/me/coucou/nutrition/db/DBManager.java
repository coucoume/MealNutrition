package me.coucou.nutrition.db;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.db.dao.TagDao;

/**
 * Created by matias on 3/10/14.
 */
public final class DBManager {

    private static DBManager mInstance = null;

    private SQLiteOpenHelper mDBHelper;
    private SQLiteDatabase mDataBase;

    private MealDao mealDao;
    private TagDao tagDao;

    private DBManager(){}

    public static DBManager getInstance(){
        if(mInstance == null){
            mInstance = new DBManager();
        }

        return mInstance;
    }

    public void init(SQLiteOpenHelper dBHelper){
        mDBHelper = dBHelper;
    }


    public void open() throws SQLException {
        mDataBase = mDBHelper.getWritableDatabase();
    }

    public void close() {
        mDBHelper.close();
    }

    public MealDao getMealDao(){
        if(this.mealDao == null){
            this.mealDao = new MealDao(mDBHelper);
        }
        return this.mealDao;
    }

    public TagDao getTagDao(){
        if(this.tagDao == null){
            this.tagDao = new TagDao(mDBHelper);
        }
        return this.tagDao;
    }

}
