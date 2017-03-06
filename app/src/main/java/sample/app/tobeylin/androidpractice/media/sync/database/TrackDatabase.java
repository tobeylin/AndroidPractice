package sample.app.tobeylin.androidpractice.media.sync.database;

import com.raizlabs.android.dbflow.annotation.Database;

@Database(name = TrackDatabase.DB_NAME, version = TrackDatabase.DB_VERSION)
public class TrackDatabase {

    public static final String DB_NAME = "android_practice_track";
    public static final int DB_VERSION = 1;

}