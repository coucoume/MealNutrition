package me.coucou.nutrition.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.List;

import me.coucou.nutrition.R;
import me.coucou.nutrition.model.Meal;

/**
 * Created by matias on 2/18/14.
 */
public class MealListAdapter extends ArrayAdapter<Meal> {

    private final Activity context;
    private final List<Meal> list;

    public MealListAdapter(Activity context, List<Meal> list) {
        super(context, R.layout.meal_viewlist_row, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        public TextView date;
        public TextView description;
        public ImageView thumb;
        public long id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;

        if (rowView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.meal_viewlist_row, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.date = (TextView) rowView.findViewById(R.id.txtMealDateTime);
            viewHolder.description = (TextView) rowView.findViewById(R.id.txtMeal);
            viewHolder.thumb = (ImageView) rowView.findViewById(R.id.imgviewMeal);
            rowView.setTag(viewHolder);
        }

        ViewHolder holder = (ViewHolder) rowView.getTag();
        Meal meal = list.get(position);
        holder.date.setText(meal.getFullDateTime());
        holder.description.setText(meal.getDescription()+"\n"+ meal.getImagePath());
        holder.id = meal.getId();

        //TODO: REmove and move to a background thread
        File imgFile = new  File(meal.getImagePath());
        if(imgFile.exists()){

            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            ImageView myImage = holder.thumb;
            myImage.setImageBitmap(myBitmap);
        }

        return rowView;
    }


}
