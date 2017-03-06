package sample.app.tobeylin.androidpractice.media.sync.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.FlowSQLiteOpenHelper;

import sample.app.tobeylin.androidpractice.R;

public class TrackDBHelper extends FlowSQLiteOpenHelper {

    private static final String TAG = TrackDBHelper.class.getSimpleName();

    private Context context;

    public TrackDBHelper(DatabaseDefinition databaseDefinition, DatabaseHelperListener listener, Context context) {
        super(databaseDefinition, listener);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        super.onCreate(db);
        Log.i(TAG, "start to insert default data");
        DatabaseUtil.executeSQLFromFile(context, db, R.raw.insert_v1);
    }

}
