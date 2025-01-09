package com.example.videoplayer;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "VideoDatabase";
    public static final String TABLE_VIDEOS = "datatable";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_THUMBNAIL = "thumbnail";
    private Context context;

    private static final String TAG = "database";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_VIDEOS + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_TITLE + " TEXT, " +
                    COLUMN_PATH + " TEXT, " +
                    COLUMN_THUMBNAIL + " TEXT" +
                    ");";

    public database(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_VIDEOS);
        onCreate(db);
    }

    public void adddata(String title, String path, String thumbnailUri) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cont = new ContentValues();
        cont.put(COLUMN_TITLE, title);
        cont.put(COLUMN_PATH, path);
        cont.put(COLUMN_THUMBNAIL, thumbnailUri);
        try {
            db.insert(TABLE_VIDEOS, null, cont);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting data", e);
        } finally {
            db.close();
        }
    }
    public ArrayList<String> getAllVideoTitles() {
        ArrayList<String> titles = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(TABLE_VIDEOS,
                    new String[]{COLUMN_TITLE},
                    null, null, null, null, COLUMN_TITLE + " ASC");

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                    titles.add(title);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e("Database", "Error fetching video titles", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return titles;
    }



    public ArrayList<ControllerVideo> fetch() {
        ArrayList<ControllerVideo> videos = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_VIDEOS,
                    null, null, null, null, null, COLUMN_TITLE + " ASC");

            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(COLUMN_TITLE));
                    @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(COLUMN_PATH));
                    @SuppressLint("Range") String thumbnail = cursor.getString(cursor.getColumnIndex(COLUMN_THUMBNAIL));
                    ControllerVideo video = new ControllerVideo(title, path, thumbnail);
                    videos.add(video);
                }
            } else {
                Toast.makeText(context, "No videos found in the database", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error fetching data", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return videos;
    }
}
