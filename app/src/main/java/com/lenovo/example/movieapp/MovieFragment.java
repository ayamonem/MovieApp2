package com.lenovo.example.movieapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.lenovo.example.movieapp.data.MovieContract;

/**
 * A placeholder fragment containing a simple view.
 */
public  class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    MovieAdapter adapter;
    GridView gridView;
    public static final String LOG_TAG = MovieFragment.class.getSimpleName();
    private int mPosition = GridView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";
    private static final int MOVIE_LOADER = 0;

    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry._ID,
            MovieContract.MoviesEntry.COLUMN_IMAGE_PATH,
            MovieContract.MoviesEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to DETAIL_COLUMNS.
    public static final int COL_IMAGE_PATH = 1;
    public static final int COL_MOVIE_ID = 2;
    public MovieFragment() {
    }

    //interface its importace is for sending data from activity to its fragment in case of tablet view
    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        adapter = new MovieAdapter(getActivity(), null, 0);
        gridView = (GridView) rootView.findViewById(R.id.movie_image_gridview);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    int movieId = cursor.getInt(COL_MOVIE_ID);
                    ((Callback) getActivity())
                            .onItemSelected(MovieContract.MoviesEntry.buildMovieURL(movieId));
                }
                mPosition = position;
            }

        });
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }
        return rootView;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    void onSortByChange(){
        getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        Uri MoviesUri =  MovieContract.MoviesEntry.buildMoviesURL();
        String sortOrder;
        if(MovieAdapter.getSortBy(getActivity()).equals("vote_average.desc")){
            sortOrder = MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " DESC";
        }else if(MovieAdapter.getSortBy(getActivity()).equals("favorite_movie.desc")){
            sortOrder = MovieContract.MoviesEntry.COLUMN_FAVORITE + " DESC";
        }
        else{
            sortOrder = null;
        }
        return new CursorLoader(getActivity(),
                MoviesUri,
                DETAIL_COLUMNS,
                null,
                null,
                sortOrder);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
        if (mPosition != GridView.INVALID_POSITION) {
            // If we don't need to restart the loader, and there's a desired position to restore
            // to, do so now.
            gridView.smoothScrollToPosition(mPosition);
        }
    }
    @Override
    public void onLoaderReset(Loader<Cursor>  loader) {adapter.swapCursor(null);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to gridView.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != gridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

}