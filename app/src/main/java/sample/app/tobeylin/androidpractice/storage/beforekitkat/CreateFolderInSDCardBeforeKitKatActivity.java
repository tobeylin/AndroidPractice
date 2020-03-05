package sample.app.tobeylin.androidpractice.storage.beforekitkat;

import android.content.Context;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.BuildConfig;
import sample.app.tobeylin.androidpractice.R;

public class CreateFolderInSDCardBeforeKitKatActivity extends AppCompatActivity {

    private static final String TAG = CreateFolderInSDCardBeforeKitKatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);

        final EditText folderPathEditText = (EditText) findViewById(R.id.createFolder_folderPathEditText);
        Button createButton = (Button) findViewById(R.id.createFolder_createButton);
        Button createFileInPackageFolderButton = (Button) findViewById(R.id.createFolder_createFileInPackageFolderButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolderInSDCard(CreateFolderInSDCardBeforeKitKatActivity.this, folderPathEditText.getText().toString());
            }
        });
        createFileInPackageFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileInPackageFolder(CreateFolderInSDCardBeforeKitKatActivity.this);
            }
        });
    }

    private void createFolderInSDCard(Context context, String folderPath) {
        File folderInSDCard = new File(folderPath);
        boolean isSuccessful = folderInSDCard.mkdir();
        if (isSuccessful) {
            Toast.makeText(context, "Successful to create folder " + folderPath, Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to create folder " + folderPath, Toast.LENGTH_LONG).show();
        }
    }

    private void createFileInPackageFolder(Context context) {
        List<String> mountPoints = getAllMountPoint(context);
        for (String mountPoint : mountPoints) {
            File publicPackageDir = createPackageFolder(mountPoint);

            if (publicPackageDir != null) {
                File tmpFile = new File(publicPackageDir.getAbsolutePath() + File.separator + "test.txt");
                try {
                    if (tmpFile.createNewFile()) {
                        Toast.makeText(context, "Successful to create file " + tmpFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, "Failed to create file " + tmpFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failed to create file " + tmpFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Failed to create package folder in " + mountPoint, Toast.LENGTH_LONG).show();
            }
        }
    }

    private File createPackageFolder(String mountPoint) {
        final String publicPackageDirPath = mountPoint + File.separator + "Android" + File.separator + "data" + File.separator + BuildConfig.APPLICATION_ID + File.separator;
        File publicPackageDir = new File(publicPackageDirPath);
        if (!publicPackageDir.exists()) {
            return publicPackageDir.mkdirs() ? publicPackageDir : null;
        } else {
            return publicPackageDir;
        }
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
