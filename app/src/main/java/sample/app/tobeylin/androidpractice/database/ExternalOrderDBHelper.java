package sample.app.tobeylin.androidpractice.database;

import android.content.Context;
import android.content.ContextWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.media.sync.database.DatabaseUtil;

public class ExternalOrderDBHelper extends SQLiteOpenHelper {

    private static final String TAG = ExternalOrderDBHelper.class.getSimpleName();

    private static final String DB_NAME = "order.db";
    private static final int DB_VERSION = 1;
    private String dbFilePath;

    private Context context;

    public ExternalOrderDBHelper(Context context) {
        super(new DatabaseContext(context), DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    public String getDbFilePath() {
        if (dbFilePath == null) {
            File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null);
            dbFilePath = externalStorageFiles[1].getAbsolutePath() + File.separator + DB_NAME;
        }
        return dbFilePath;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseUtil.executeSQLFromFile(context, db, R.raw.create_customers_external_v1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static class DatabaseContext extends ContextWrapper {

        public DatabaseContext(Context base) {
            super(base);
        }

        @Override
        public File getDatabasePath(String name) {
            File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(this, null);
            File databaseFile = new File(externalStorageFiles[1].getAbsolutePath() + File.separator + name);
            if (!databaseFile.getParentFile().exists()) {
                databaseFile.getParentFile().mkdirs();
            }

            Log.i(TAG, "External path = " + externalStorageFiles[1].getAbsolutePath());

            return databaseFile;
        }

        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory, DatabaseErrorHandler errorHandler) {
            return openOrCreateDatabase(name, mode, factory);
        }

        @Override
        public SQLiteDatabase openOrCreateDatabase(String name, int mode, SQLiteDatabase.CursorFactory factory) {
            return SQLiteDatabase.openOrCreateDatabase(getDatabasePath(name), null);
        }

    }

}
