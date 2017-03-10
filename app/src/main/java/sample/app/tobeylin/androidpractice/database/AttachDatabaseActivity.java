package sample.app.tobeylin.androidpractice.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import sample.app.tobeylin.androidpractice.MainApplication;
import sample.app.tobeylin.androidpractice.R;

public class AttachDatabaseActivity extends AppCompatActivity {

    private static final String TAG = AttachDatabaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_database);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.attachDatabase_recyclerView);

        OrderAdapter orderAdapter = new OrderAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(orderAdapter);

        Cursor cursor = attachExternalDatabase((MainApplication) getApplication());
        orderAdapter.setCursor(cursor);
    }

    private Cursor attachExternalDatabase(MainApplication application) {
        CustomerDBHelper customerDBHelper = application.getCustomerDBHelper();
        ExternalOrderDBHelper externalOrderDBHelper = application.getExternalOrderDBHelper();
        Cursor cursor = null;

        try {
            SQLiteDatabase db = customerDBHelper.getWritableDatabase();
            db.execSQL("ATTACH DATABASE '" + externalOrderDBHelper.getDbFilePath() + "' AS table2");

            cursor = db.rawQuery("SELECT table2.orders._id AS order_id, customers.name AS customer_name, table2.orders.item AS item FROM table2.orders INNER JOIN customers ON table2.orders.customer_id = customers._id", null);
        } catch (Exception e) {
            Log.e(TAG, "External DB file path = " + externalOrderDBHelper.getDbFilePath());
            e.printStackTrace();
        }

        return cursor;
    }

}
