package sample.app.tobeylin.androidpractice.storage.afterkitkat;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.storage.MountPointAdapter;

public class MountPointAfterKitKatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount_point);

        TextView textView = (TextView) findViewById(R.id.mountPoint_titleTextView);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mountPoint_recycleView);

        textView.setText("Get Mount Points By ContextCompat After KitKat");
        MountPointAdapter mountPointAdapter = new MountPointAdapter(getAllMountPoint(this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mountPointAdapter);
    }

    private List<String> getAllMountPoint(Context context) {
        List<String> mountPoints = new ArrayList<>();
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null);
        for (File external : externalStorageFiles) {
            String mountPoint = getMountPoint(external);
            if (mountPoint != null) {
                mountPoints.add(mountPoint);
            }
        }
        return mountPoints;
    }

    private String getMountPoint(File file) {
        String mountPoint = null;
        if (file != null) {
            final long totalSpace = file.getTotalSpace();
            while (true) {
                File parentFile = file.getParentFile();
                if (parentFile == null || parentFile.getTotalSpace() != totalSpace) {
                    mountPoint = file.getAbsolutePath();
                    break;
                }
                file = parentFile;
            }
        }
        return mountPoint;
    }
}
