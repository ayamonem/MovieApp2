package com.lenovo.example.movieapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.lenovo.example.movieapp.sync.MoviesSyncAdapter;

public class MainActivity extends ActionBarActivity implements MovieFragment.Callback {

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane; //choose for table when true or false when phone
    String mSortBy;//sort string for movies when settings parameter changed
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSortBy = MovieAdapter.getSortBy(this);
        if (findViewById(R.id.movie_detail_container) != null) {//check if it is tablet view
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailActivityFragment(), DETAILFRAGMENT_TAG)
                        .commit();
            }
        } else {//else it is phone view
            mTwoPane = false;
           getSupportActionBar().setElevation(0f);//adding actionbar

        }
      MoviesSyncAdapter.initializeSyncAdapter(this);//sync from internet url every hour
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {//when it is tablet view we will send its detailview movie data to DetailActivityFragment

            Bundle args = new Bundle();
            args.putParcelable(DetailActivityFragment.DETAIL_URI, contentUri);

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {//else we wont send to detail fragment we will use intent only for sending to DetailActivity
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onResume() {
        super.onResume();
        String sortBy = MovieAdapter.getSortBy(this);
        if(sortBy != null && !sortBy.equals(mSortBy)){
            MovieFragment mf = (MovieFragment)getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
            if(mf != null){
                mf.onSortByChange();//use this for sorting movies from database  accroding to settings
            }
        }
        mSortBy = sortBy;

    }


}
