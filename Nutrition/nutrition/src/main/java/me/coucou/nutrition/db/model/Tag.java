package me.coucou.nutrition.db.model;

/**
 * Created by matias on 3/6/14.
 */
public class Tag {

    private String mLabel;
    private int mId;
    private int mCalories;

    public Tag(String label, int id, int calories){

        mLabel = label;
        mId = id;
        mCalories = calories;
    }

    public String getLabel(){
        return mLabel;
    }

    public int getId(){
        return mId;
    }

    public int getCalories(){
        return mCalories;
    }

    public String toString(){
        return "label:"+ mLabel +" id:"+mId+" calories:"+mCalories;
    }
}
