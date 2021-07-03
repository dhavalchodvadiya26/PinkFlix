package com.example.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

import androidx.annotation.Nullable;

import com.example.itemmodels.ItemMovie;
import com.example.itemmodels.ItemRecent;

public class OfflineDatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MovieDetails";
    private static final String TABLE_MOVIE = "movie_videos";
    private static final String MOVIE_ID = "movie_id";
    private static final String MOVIE_NAME = "movie_name";
    private static final String MOVIE_DURATION = "movie_duration";
    private static final String MOVIE_IMAGE = "movie_image";
    private static final String MOVIE_URL = "movie_url";

    private static final String TABLE_MOVIE1 = "movie_videos1";
    private static final String MOVIE_ID1 = "movie_id1";
    private static final String MOVIE_DURATION1 = "movie_duration1";

    private static final String TABLE_MOVIE2 = "movie_videos2";
    private static final String MOVIE_ID2 = "movie_id2";
    private static final String MOVIE_DURATION2 = "movie_duration2";


    public OfflineDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_MOVIE + "(" + MOVIE_ID + " INTEGER PRIMARY KEY," +
                MOVIE_NAME + " VARCHAR(255)," + MOVIE_DURATION + " VARCHAR(255), " + MOVIE_IMAGE + " VARCHAR(255)," + MOVIE_URL + " VARCHAR(255)" + ")";
        db.execSQL(CREATE_TABLE);

        String CREATE_TABLE1 = "CREATE TABLE IF NOT EXISTS " + TABLE_MOVIE1 + "(" + MOVIE_ID1 + " INTEGER PRIMARY KEY," + MOVIE_DURATION1 + " VARCHAR(255)" + ")";
        db.execSQL(CREATE_TABLE1);

        String CREATE_TABLE2 = "CREATE TABLE IF NOT EXISTS " + TABLE_MOVIE2 + "(" + MOVIE_ID2 + " INTEGER PRIMARY KEY," + MOVIE_DURATION2 + " VARCHAR(255)" + ")";
        db.execSQL(CREATE_TABLE2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        onCreate(db);
    }

    public void addContacts(ItemMovie itemMovie) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID, itemMovie.getMovieId());
        contentValues.put(MOVIE_NAME, itemMovie.getMovieName());
        contentValues.put(MOVIE_DURATION, itemMovie.getMovieDuration());
        contentValues.put(MOVIE_IMAGE, itemMovie.getMovieImage());
        contentValues.put(MOVIE_URL, itemMovie.getMovieUrl());
        db.insert(TABLE_MOVIE, null, contentValues);
        db.close();
    }

    public void addDuration(String movie_id, long movie_duration) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID1, movie_id);
        contentValues.put(MOVIE_DURATION1, movie_duration);
        db.insert(TABLE_MOVIE1, null, contentValues);
        db.close();
    }

    public void addDuration2(String movie_id, long movie_duration) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MOVIE_ID2, movie_id);
        contentValues.put(MOVIE_DURATION2, movie_duration);
        db.insert(TABLE_MOVIE2, null, contentValues);
        db.close();
    }

    public void updateMovieDuration(String movie_id, long movie_duration) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "UPDATE " + TABLE_MOVIE1 + " SET " + MOVIE_DURATION1 + " = " + movie_duration + " WHERE " + MOVIE_ID1 + " = " + movie_id;
        db.beginTransaction();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            stmt.execute();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

    }

    public boolean checkIfMyMovieExists(String movie_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String Query = "Select * from " + TABLE_MOVIE1 + " where " + MOVIE_ID1 + " = " + "'" + movie_id + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public boolean checkIfMyMovieExists2(String movie_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String Query = "Select * from " + TABLE_MOVIE2 + " where " + MOVIE_ID2 + " = " + "'" + movie_id + "'";
        Cursor cursor = db.rawQuery(Query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public ItemMovie getMovieByID2(String movie_id) {
        ItemMovie itemRecent = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIE2, new String[]{MOVIE_ID2, MOVIE_DURATION2},
                MOVIE_ID2 + "=?", new String[]{movie_id}, null,
                null, null, null);


        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                System.out.println("Cursor ==> " + cursor);
                itemRecent = new ItemMovie(cursor.getString(0), cursor.getLong(1));
                System.out.println("getMovieByID2 ==> Movie_ID2 ==> " + cursor.getString(0));
                System.out.println("getMovieByID2 ==> Movie_DURATION ==> " + cursor.getLong(1));
            } else {
                itemRecent = new ItemMovie("0", 0);
            }
        }
        return itemRecent;
    }

    public ItemRecent getMovieByID1(String movie_id) {
        ItemRecent itemRecent = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIE1, new String[]{MOVIE_ID1, MOVIE_DURATION1},
                MOVIE_ID1 + "=?", new String[]{movie_id}, null,
                null, null, null);


        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                System.out.println("Cursor ==> " + cursor);
                itemRecent = new ItemRecent(cursor.getString(0), cursor.getLong(1));
                System.out.println("Movie_ID1 ==> " + cursor.getString(0));
                System.out.println("Movie_DURATION ==> " + cursor.getLong(1));
            } else {
                itemRecent = new ItemRecent("0", 0);
            }
        }
        return itemRecent;
    }

    public ItemMovie getMovieByURL(String URL) {
        ItemMovie itemMovie = null;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_MOVIE, new String[]{MOVIE_ID, MOVIE_NAME, MOVIE_DURATION, MOVIE_IMAGE, MOVIE_URL},
                MOVIE_URL + "=?", new String[]{String.valueOf(URL)}, null,
                null, null, null);

        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                System.out.println("Cursor ==> " + cursor);
                itemMovie = new ItemMovie(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
                System.out.println("Movie_ID1 ==> " + cursor.getString(0));
                System.out.println("Movie_DURATION ==> " + cursor.getLong(1));
            } else {
                itemMovie = new ItemMovie("0", "0", "0", "0", "0");
            }
        }
        return itemMovie;
    }
}
