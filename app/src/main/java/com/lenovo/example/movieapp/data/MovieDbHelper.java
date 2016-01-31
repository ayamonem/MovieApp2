package com.lenovo.example.movieapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;



/**
 * Created by user on 31/07/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MoviesEntry.TABLE_NAME + " (" +
                MovieContract.MoviesEntry._ID + " INTEGER PRIMARY KEY," +
                MovieContract.MoviesEntry.COLUMN_IMAGE_PATH + " TEXT UNIQUE NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.MoviesEntry.COLUMN_FAVORITE + " INTEGER NULL, " +
                MovieContract.MoviesEntry.COLUMN_VOTE_AVERAGE + " TEXT NOT NULL " +
                " );";

        final String  SQL_CREATE_VIDEO_TABLE = "CREATE TABLE " + MovieContract.VideoEntry.TABLE_NAME + " (" +
                MovieContract.VideoEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.VideoEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_VIDEO_ID + " TEXT  NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_MOVIE_NAME + " TEXT NOT NULL, " +
                MovieContract.VideoEntry.COLUMN_ADDRESS + " TEXT NOT NULL " +
                " );";
        final String SQL_CREATE_REVIEW_TABLE = "CREATE TABLE " + MovieContract.ReviewEntry.TABLE_NAME + " ("+
                MovieContract.ReviewEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                MovieContract.ReviewEntry.COLUMN_REVIEW + " TEXT NOT NULL " +
                " );";


        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_VIDEO_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_REVIEW_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MoviesEntry.TABLE_NAME );
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.VideoEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.ReviewEntry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
