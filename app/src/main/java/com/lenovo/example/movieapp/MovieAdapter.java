package com.lenovo.example.movieapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class MovieAdapter extends CursorAdapter {

    private Context mContext;
    String baseURL = "http://image.tmdb.org/t/p/w185/";
        public MovieAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
            mContext = context;
        }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_list_item, parent, false);
        return view;

    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String image = cursor.getString(MovieFragment.COL_IMAGE_PATH);
        String imageURL = baseURL + image;
        ImageView movieImageView = (ImageView) view.findViewById(R.id.list_item_icon);
        Picasso.with(context).load(imageURL).into(movieImageView);
    }
    public static String getSortBy(Context context) {//this method get string from settings menu
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_sort_key),
                context.getString(R.string.pref_sort_default));
    }
}
