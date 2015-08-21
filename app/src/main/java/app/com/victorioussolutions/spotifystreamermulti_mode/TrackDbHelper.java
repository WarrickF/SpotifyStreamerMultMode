package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Warrick on 8/19/2015.
 */
public class TrackDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 2;
    public static final String DATABASE_NAME = "Tracks.db";


    private static final String TEXT_TYPE = " TEXT";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + TracksContract.TrackEntry.TABLE_NAME + " (" +
                    TracksContract.TrackEntry._ID + " INTEGER PRIMARY KEY," +
                    TracksContract.TrackEntry.COLUMN_NAME_ARTIST_ID + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_ARTIST_NAME + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_ALBUM_NAME + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_ALBUM_ARTWORK + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_TRACK_NAME + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_TRACK_DURATION + TEXT_TYPE + COMMA_SEP +
                    TracksContract.TrackEntry.COLUMN_NAME_PREVIEW_URL + TEXT_TYPE  +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + TracksContract.TrackEntry.TABLE_NAME;



    public TrackDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}