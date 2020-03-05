package sample.app.tobeylin.androidpractice.storage.beforekitkat;

import android.content.Context;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.storage.MountPointAdapter;

public class MountPointBeforeKitKatActivity extends AppCompatActivity {

    private static final String TAG = MountPointBeforeKitKatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount_point);

        TextView textView = (TextView) findViewById(R.id.mountPoint_titleTextView);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mountPoint_recycleView);

        textView.setText("Get Mount Points Before KitKat");
        MountPointAdapter mountPointAdapter = new MountPointAdapter(getAllMountPoint(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mountPointAdapter);
    }

    private List<String> getAllMountPoint(Context context) {
        List<String> mountPoints = new ArrayList<>();

        try {
            Object[] storageVolumes = getStorageVolumeList(context);

            for (Object storageVolume : storageVolumes) {
                StorageVolumeWrapper wrapper = new StorageVolumeWrapper(storageVolume);
                String mountPoint = wrapper.getFilePath();

                if (isMountPointAvailable(mountPoint)) {
                    mountPoints.add(mountPoint);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Can't get mount points by storage manager");
            Toast.makeText(context, "Can't get mount points by storage manager", Toast.LENGTH_LONG).show();
        }

        return mountPoints;
    }

    private boolean isMountPointAvailable(String mountPoint) {
        return new File(mountPoint).canRead();
    }

    private Object[] getStorageVolumeList(Context context) throws Exception {
        try {
            final Object service = context.getSystemService(Context.STORAGE_SERVICE);
            return (Object[]) StorageManager.class.getMethod("getVolumeList").invoke(service);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class StorageVolumeWrapper {

        private Object storageVolume;

        public StorageVolumeWrapper(Object storageVolume) {
            this.storageVolume = storageVolume;
        }

        public String getFilePath() {
            try {
                return (String) getStorageVolumeClass().getMethod("getPath").invoke(storageVolume);
            } catch (Exception e) {
                throw new RuntimeException("StorageVolume#getPath() should be available", e);
            }
        }

        private Class<?> getStorageVolumeClass() {
            try {
                return Class.forName("android.os.storage.StorageVolume");
            } catch (Exception e) {
                throw new RuntimeException("class StorageVolume not found", e);
            }
        }
    }

}
