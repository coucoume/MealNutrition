package me.coucou.nutrition.db.dao;

import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;
import java.util.Iterator;

import me.coucou.nutrition.model.Tag;

/**
 * Created by matias on 3/30/14.
 */
public class TagDao extends BaseDao {

    //Hash to handle meal TAGS
    private HashMap<String, Tag> mTags = new HashMap<String, Tag>();
    private HashMap<String, Tag> mListedTags = new HashMap<String, Tag>();


    public TagDao(SQLiteOpenHelper helper) {
        super(helper);

        mTags.put("papa", new Tag("papa", 1, 10));
        mTags.put("carne", new Tag("carne", 2, 20));
        mTags.put("pescado", new Tag("pescado", 3, 30));
        mTags.put("pollo", new Tag("pollo", 4, 40));
        mTags.put("lechuga", new Tag("lechuga", 5, 50));

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
            Log.d(this.toString(), "key in tag list:" + key);
        }
    }
}
