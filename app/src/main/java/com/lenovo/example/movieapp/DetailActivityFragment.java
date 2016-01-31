package com.lenovo.example.movieapp;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lenovo.example.movieapp.data.MovieContract;
import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class
        DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        private static final int DETAIL_LOADER = 0;
        private Uri mUri;
        static final String DETAIL_URI = "URI";
        String key;
        String baseURL = "http://image.tmdb.org/t/p/w185/";
        private ShareActionProvider mShareActionProvider;
        String movieTitle;

        private static final String[] DETAIL_COLUMNS = {
                MovieContract.MoviesEntry.TABLE_NAME + "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID,
                MovieContract.MoviesEntry.COLUMN_IMAGE_PATH,
                MovieContract.MoviesEntry.COLUMN_MOVIE_NAME,
                MovieContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW,
                MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE,
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE,
                MovieContract.ReviewEntry.COLUMN_REVIEW,
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                MovieContract.VideoEntry.COLUMN_ADDRESS,
                MovieContract.VideoEntry.COLUMN_MOVIE_NAME,
                MovieContract.MoviesEntry.COLUMN_FAVORITE

                };
        // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
        // must change.
        public static final int COL_IMAGE_PATH = 1;
        public static final int COL_MOVIE_NAME = 2;
        public static final int COL_MOVIE_OVERVIEW = 3;
        public static final int COL_VOTE_AVERAGE = 4;
        public static final int COL_RELEASE_DATE = 5;
        public static final int COL_REVIEW = 6;
        public static final int COL_AUTHOR = 7;
        public static final int COL_ADDRESS = 8;
        public static final int COL_NAME = 9;
        public static final int COL_FAVORITE = 10;

        //Details of Movie in Layout fragment_movies_detail
        private ImageView mMovieImage;
        private TextView mMovieTitle;
        private TextView mReleaseYear;
        private TextView mVoteAverage;
        private TextView mMovieOverview;
        private TextView mReviewTV;
        private TextView mNameTV;
        private ImageView mVideoIV;
        private ImageView mFavoriteIV;

public DetailActivityFragment() {
        setHasOptionsMenu(true);
        }
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container,
        Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
        //get argument from detailActivity for movie details
        mUri = arguments.getParcelable(DetailActivityFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_movies_detail, container, false);
        //movie
        mMovieImage = (ImageView) rootView.findViewById(R.id.detail_icon);
        mMovieTitle = (TextView) rootView.findViewById(R.id.title_textview);
        mReleaseYear = (TextView) rootView.findViewById(R.id.release_year_textview);
        mVoteAverage = (TextView) rootView.findViewById(R.id.vote_average_textview);
        mMovieOverview = (TextView) rootView.findViewById(R.id.movie_overview_textview);
        mFavoriteIV= (ImageView)rootView.findViewById(R.id.favorite_image_view);
        mFavoriteIV.setOnClickListener(new View.OnClickListener() {

public void onClick(View button) {
        //Set the button's appearance
        button.setSelected(!button.isSelected());

        //if favourit button selected then adding in database value 1 for the selected movie
        if (button.isSelected()) {
        ContentValues favorite = new ContentValues();
        favorite.put(MovieContract.MoviesEntry.COLUMN_FAVORITE, 1);
        getActivity().getContentResolver().update(MovieContract.MoviesEntry.buildMoviesURL(),
        favorite,
        MovieContract.MoviesEntry.TABLE_NAME +
        "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ",
        new String[]{Integer.toString(MovieContract.MoviesEntry.getMovieIdFromUri(mUri))});

        }else if(!button.isSelected()){//else add zero value for favourit not selected movie
        ContentValues favorite = new ContentValues();
        favorite.put(MovieContract.MoviesEntry.COLUMN_FAVORITE, 0);
        getActivity().getContentResolver().update(MovieContract.MoviesEntry.buildMoviesURL(),
        favorite,
        MovieContract.MoviesEntry.TABLE_NAME +
        "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ",
        new String[]{Integer.toString(MovieContract.MoviesEntry.getMovieIdFromUri(mUri))});
        }
        }
        });

        //review
        mReviewTV = (TextView)rootView.findViewById(R.id.list_item_review);
        //video
        mNameTV = (TextView)rootView.findViewById(R.id.list_item_trailer);
        mVideoIV = (ImageView)rootView.findViewById(R.id.list_item_video);
        mVideoIV.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                //when user click on movie trailer image he will be directed to youtube url
                watchYoutubeVideo(getActivity(), key);
                }
                });
        return rootView;
        }
        public static void watchYoutubeVideo(Context context, String videoID){
                try{
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoID));
                context.startActivity(i);
                }catch (ActivityNotFoundException e){
                // watch trailer video from youtube and add key from database
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoID));
                context.startActivity(i);
                }
        }
        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
                        // Inflate the menu; this adds items to the action bar if it is present.
                inflater.inflate(R.menu.menu_detail, menu);
        }
        private Intent createShareVideoURL() {
                //share intent for youtube video and adding key value from database
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, movieTitle + "  "
                + "http://www.youtube.com/watch?v=" + key);
                return shareIntent;
        }
        @Override
        public void onActivityCreated(Bundle savedInstanceState) {
                getLoaderManager().initLoader(DETAIL_LOADER, null, this);
                super.onActivityCreated(savedInstanceState);
        }
        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
                if (mUri != null) {
                return new CursorLoader( getActivity(),
                mUri,
                DETAIL_COLUMNS,
                null,
                null,
                null
                );
                }
                return null;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
                if (data != null && data.moveToFirst()) {
        //Getting data movies from database sqlite by using indices
                movieTitle = data.getString(COL_MOVIE_NAME);
                mMovieTitle.setText(movieTitle);

                String image = data.getString(COL_IMAGE_PATH);
                String imageURL = baseURL + image;
                Picasso.with(getActivity()).load(imageURL).into(mMovieImage);
                String movieDate = data.getString(COL_RELEASE_DATE);
                mReleaseYear.setText(movieDate.substring(0, 4));
                String voteAverage = data.getString(COL_VOTE_AVERAGE);
                mVoteAverage.setText(voteAverage + "/10");

                String movieOverview = data.getString(COL_MOVIE_OVERVIEW);
                mMovieOverview.setText(movieOverview);

                int favorite = data.getInt(COL_FAVORITE);
                if(favorite == 1){
                mFavoriteIV.setSelected(true);
                }else{
                mFavoriteIV.setSelected(false);
                }

                //reviews
                String  review = data.getString(COL_REVIEW);
                String author = data.getString(COL_AUTHOR);
                if (author.equals(".")) {
                mReviewTV.setText(review + ".");
                }else{
                mReviewTV.setText(review + "  By " + author);
                }


                //video
                key = data.getString(COL_ADDRESS);
                String name = data.getString(COL_NAME);
                mNameTV.setText(name);

                if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareVideoURL());
                }


                }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> cursorLoader) {
                }
        }
