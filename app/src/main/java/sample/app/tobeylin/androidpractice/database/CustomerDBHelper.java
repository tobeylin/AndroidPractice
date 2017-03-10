package sample.app.tobeylin.androidpractice.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.media.sync.database.DatabaseUtil;

public class CustomerDBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "customers.db";
    private static final int DB_VERSION = 1;

    private Context context;

    public CustomerDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DatabaseUtil.executeSQLFromFile(context, db, R.raw.create_customers_internal_v1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
