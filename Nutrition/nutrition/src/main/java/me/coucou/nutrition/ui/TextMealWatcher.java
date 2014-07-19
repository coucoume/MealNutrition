package me.coucou.nutrition.ui;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

/**
 * Created by matias on 6/11/14.
 */
public class TextMealWatcher implements TextWatcher {

    private Context mContext;

    public TextMealWatcher(Context context){
        mContext = context;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        Log.d("CreateMealActivity",
                "beforeTextChanged:" + s + " start:" + start + " count" + count + " after:" + after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        Log.d("CreateMealActivity",
                "onTextChanged:"+ s + " start:"+start +" before:"+before + " count" + count );
    }

    @Override
    public void afterTextChanged(Editable s) {
        Log.d("CreateMealActivity",
                "afterTextChanged:"+ s );
        /*
        String wordsSpaceSplit[] = s.toString().split(" ");
        //TODO: Make it performance
        for (int i=0; i < wordsSpaceSplit.length; i++ ){
            Log.d("CreateMealActivity",
                    "wordsSpaceSplit:"+ wordsSpaceSplit[i] );

            Tag tag = tagDao.getTagByLabel(wordsSpaceSplit[i]);

            if( tag != null && tagDao.tagToList(tag)){
                Log.i("FOUND TEXT FOR TAG", tag.toString());
                tagDao.printListedTags();
                Log.i("FOUND TEXT FOR TAG", "--------------------------------------");
                Button btnTag = new Button(getActivity());
                btnTag.setText(tag.getLabel());
                LinearLayout ll = (LinearLayout) getActivity().findViewById(R.id.tagContainer);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                ll.addView(btnTag, lp);
            }
        }
        */
    }


}
