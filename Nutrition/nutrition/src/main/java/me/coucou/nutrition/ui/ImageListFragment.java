package me.coucou.nutrition.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.List;

import me.coucou.nutrition.CreateMealActivity;
import me.coucou.nutrition.NutritionApplication;
import me.coucou.nutrition.R;
import me.coucou.nutrition.db.DBManager;
import me.coucou.nutrition.db.dao.MealDao;
import me.coucou.nutrition.image.ImageCache;
import me.coucou.nutrition.image.ImageFetcher;
import me.coucou.nutrition.image.RecyclingImageView;
import me.coucou.nutrition.model.Meal;

/**
 * Created by matias on 10/10/13.
 */
public class ImageListFragment  extends Fragment implements AdapterView.OnItemClickListener{

    private MealDao mealDao;

    private static final String IMAGE_CACHE_DIR = "images";

    private int mImageThumbSize;
    private ImageAdapter mAdapter;
    private ImageFetcher mImageFetcher;

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageListFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        try {
            mealDao = DBManager.getInstance().getMealDao();
            DBManager.getInstance().open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);

        List<Meal> values = mealDao.getAllMeals();
        mAdapter = new ImageAdapter(getActivity(), values);

        ImageCache.ImageCacheParams cacheParams = new ImageCache.ImageCacheParams(getActivity(), IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.meal_placeholder);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView listView = (ListView) v.findViewById(R.id.mealList);

        TextView noMeal = (TextView) v.findViewById(R.id.txtNoMeal);

        ImageButton addBtn = (ImageButton) v.findViewById(R.id.addMeal);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createMealIntent = new Intent(getActivity(), CreateMealActivity.class);
                startActivity(createMealIntent);
            }
        });

        //SHOW the textfield that display create your first meal
        if(mealDao.getAllMeals().isEmpty()){
            noMeal.setVisibility(View.VISIBLE);
        }

        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                // Pause fetcher to ensure smoother scrolling when flinging
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_FLING) {
                    mImageFetcher.setPauseWork(true);
                } else {
                    mImageFetcher.setPauseWork(false);
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            DBManager.getInstance().open();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mImageFetcher.setExitTasksEarly(false);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        super.onPause();
        DBManager.getInstance().close();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }

    @TargetApi(16)
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

        Meal model = (Meal) mAdapter.getItem(position);

        Intent createMealIntent = new Intent(getActivity(), CreateMealActivity.class);
        NutritionApplication app = (NutritionApplication) getActivity().getApplication();
        app.currentMeal = model;
        startActivity(createMealIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    /**
     * The main adapter that backs the ListView. This is fairly standard except the number of
     * columns in the GridView is used to create a fake top row of empty views as we use a
     * transparent ActionBar and don't want the real top row of images to start off covered by it.
     */
    private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mNumColumns = 0;
        private int mActionBarHeight = 0;
        private final List<Meal> mList;
        private ListView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context, List<Meal> list) {
            super();
            mList = list;
            mContext = context;
            mImageViewLayoutParams = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            // Calculate ActionBar height
            TypedValue tv = new TypedValue();
            if (context.getTheme().resolveAttribute(
                    android.R.attr.actionBarSize, tv, true)) {
                mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data, context.getResources().getDisplayMetrics());
            }
        }

        @Override
        public int getCount() {
            // Size + number of columns for top empty row
            return mList.size() + mNumColumns;
        }

        @Override
        public Object getItem(int position) {
            return position < mNumColumns ?
                    null : mList.get(position - mNumColumns);
        }

        @Override
        public long getItemId(int position) {
            return position < mNumColumns ? 0 : position - mNumColumns;
        }

        @Override
        public int getViewTypeCount() {
            // Two types of views, the normal ImageView and the top row of empty views
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return (position < mNumColumns) ? 1 : 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup container) {

            View rowView = convertView;
            if (rowView == null) { // if it's not recycled, instantiate and initialize
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.meal_viewlist_row, container, false);
                ViewHolder vh = new ViewHolder();
                vh.date = (TextView) rowView.findViewById(R.id.txtMealDateTime);
                vh.thumb = (RecyclingImageView) rowView.findViewById(R.id.imageView);
                vh.description = (TextView) rowView.findViewById(R.id.txtMeal);

                rowView.setTag(vh);
            }

            ViewHolder holder = (ViewHolder) rowView.getTag();
            Meal meal = mList.get(position);
            holder.date.setText(meal.getFullDateTime());
            holder.description.setText(meal.getDescription());//+"\n"+ meal.getImagePath());
            holder.id = meal.getId();

            // Finally load the image asynchronously into the ImageView, this also takes care of
            // setting a placeholder image while the background thread runs
            //mImageFetcher.loadImage(mList.get(position - mNumColumns), holder.thumb);
            mImageFetcher.loadImage(meal.getImagePath(), holder.thumb);
            return rowView;
        }
    }

    static class ViewHolder {
        public TextView date;
        public TextView description;
        public RecyclingImageView thumb;
        public long id;
    }
}