package sample.app.tobeylin.androidpractice.storage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class StorageMountChangedActivity extends AppCompatActivity {

    private StorageMountReceiver storageMountReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageMountReceiver = new StorageMountReceiver();
        StorageMountReceiver.register(getApplicationContext(), storageMountReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StorageMountReceiver.unregister(getApplicationContext(), storageMountReceiver);
    }

}
