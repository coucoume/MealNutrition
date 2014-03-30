package me.coucou.nutrition.db.dao;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by matias on 3/10/14.
 */
public class BaseDao {

    protected SQLiteOpenHelper mDBHelper;

    public BaseDao(SQLiteOpenHelper helper){
        mDBHelper = helper;
    }

    protected SQLiteDatabase getDataBase(){
        return mDBHelper.getWritableDatabase();
    }

}
