package app.com.victorioussolutions.spotifystreamermulti_mode;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Warrick on 8/19/2015.
 */
public final  class TracksContract {
    public TracksContract() {};

    public static abstract class TrackEntry implements BaseColumns {
        public static final String TABLE_NAME = "Tracks";
        public static final String COLUMN_NAME_ARTIST_ID = "artist_id";
        public static final String COLUMN_NAME_ARTIST_NAME = "artist_name";
        public static final String COLUMN_NAME_ALBUM_NAME = "album_name";
        public static final String COLUMN_NAME_ALBUM_ARTWORK = "album_artwork";
        public static final String COLUMN_NAME_TRACK_NAME = "track_name";
        public static final String COLUMN_NAME_TRACK_DURATION = "track_duration";
        public static final String COLUMN_NAME_PREVIEW_URL = "preview_url";

    }
}



