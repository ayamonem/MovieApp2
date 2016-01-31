package com.lenovo.example.movieapp.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

/**
 * Created by user on 31/07/2015.
 */
public class MovieProvider extends ContentProvider {


   private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MovieDbHelper mOpenHelper;

    static final int MOVIE = 100;
    static final int MOVIE_WITH_ID = 101;
    static final int VIDEO = 200;
    static final int REVIEW =300;

    private  static final SQLiteQueryBuilder sVideoAndReviewByMovieQueryBuilder;

    static{
        sVideoAndReviewByMovieQueryBuilder = new SQLiteQueryBuilder();

        sVideoAndReviewByMovieQueryBuilder.setTables(
                MovieContract.MoviesEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.VideoEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME +
                        //
                        " ON " + MovieContract.MoviesEntry.TABLE_NAME +
                        "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.COLUMN_MOVIE_ID + " AND "
                        //
                        + MovieContract.MoviesEntry.TABLE_NAME +
                        "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " AND "
                        //
                        + MovieContract.ReviewEntry.TABLE_NAME +
                        "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID +
                        " = " + MovieContract.VideoEntry.TABLE_NAME +
                        "." + MovieContract.VideoEntry.COLUMN_MOVIE_ID);
    }



    private static final String sIDSettingSelection =
           MovieContract.MoviesEntry.TABLE_NAME +
                   "." + MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " = ? ";





    private Cursor getMoviesById(Uri uri, String[] projection, String sortOrder) {
        int  movieId = MovieContract.MoviesEntry.getMovieIdFromUri(uri);

        String selection = sIDSettingSelection;
        String[] selectionArgs = new String[]{Integer.toString(movieId)};


        return sVideoAndReviewByMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(), projection, selection,
                selectionArgs, null, null, sortOrder);
    }




    static UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = MovieContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, MovieContract.PATH_MOVIES, MOVIE);
        matcher.addURI(authority, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, MovieContract.PATH_VIDEO , VIDEO);
        matcher.addURI(authority, MovieContract.PATH_REVIEW , REVIEW);



        return matcher;
    }

    @Override
    public String getType(Uri uri) {


        final int match = sUriMatcher.match(uri);

        switch (match) {


            case MOVIE:
                return MovieContract.MoviesEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return MovieContract.MoviesEntry.CONTENT_ITEM_TYPE;
            case VIDEO:
                return MovieContract.VideoEntry.CONTENT_TYPE;
            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {

            // "movies/#
            case MOVIE_WITH_ID: {
                retCursor = getMoviesById( uri, projection, sortOrder);
                break;
            }
            // "movies"
            case MOVIE: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.MoviesEntry.TABLE_NAME, projection,selection,
                        selectionArgs,null,null,sortOrder);
                break;
            }


            //"video/"
            case VIDEO: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.VideoEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            }

            //"review/"
            case REVIEW: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        MovieContract.ReviewEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }




    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {

            case MOVIE: {
                long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME,null, values);
                if ( _id > 0 )
                    returnUri = MovieContract.MoviesEntry.buildMovieUri(_id);
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;

            }

            case REVIEW:{
                long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewURL(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }

            case VIDEO: {
                long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, values);
                if(_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewURL(_id);
                else
                    throw new SQLException("Failed to insert row into" + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowMatched ;
        switch (match){
            case MOVIE:
                rowMatched = db.delete(MovieContract.MoviesEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case REVIEW:
                rowMatched = db.delete(MovieContract.ReviewEntry.TABLE_NAME,selection, selectionArgs);
                break;
            case VIDEO:
                rowMatched = db.delete(MovieContract.VideoEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
  }

        if(rowMatched != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowMatched;
    }

    @Override
    public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs) {

            final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
            final int match = sUriMatcher.match(uri);

            int rowMatched;
            switch (match){
                case MOVIE: {
                    rowMatched = db.update(MovieContract.MoviesEntry.TABLE_NAME, values,
                            selection, selectionArgs);
                    break;
                }
                case REVIEW: {
                    rowMatched = db.update(MovieContract.ReviewEntry.TABLE_NAME, values,
                            selection, selectionArgs);
                    break;
                }
                case VIDEO: {
                    rowMatched = db.update(MovieContract.VideoEntry.TABLE_NAME, values,
                            selection, selectionArgs);
                    break;
                }
                default:
                    throw new UnsupportedOperationException("Unknown uri: " + uri);
            }


            if(rowMatched != 0){
                getContext().getContentResolver().notifyChange(uri, null);
            }

            return rowMatched;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIE:
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.MoviesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                int returnCountR = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCountR++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountR;
            case VIDEO:
                db.beginTransaction();
                int returnCountV = 0;
                try {
                    for (ContentValues value : values) {

                        long _id = db.insert(MovieContract.VideoEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCountV++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCountV;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
