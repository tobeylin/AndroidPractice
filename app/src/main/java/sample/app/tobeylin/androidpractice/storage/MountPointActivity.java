package sample.app.tobeylin.androidpractice.storage;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;

public class MountPointActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mount_point);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.mountPoint_recycleView);

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
