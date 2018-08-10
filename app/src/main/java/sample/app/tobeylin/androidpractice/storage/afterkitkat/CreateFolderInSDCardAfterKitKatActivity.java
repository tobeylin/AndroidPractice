package sample.app.tobeylin.androidpractice.storage.afterkitkat;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;

public class CreateFolderInSDCardAfterKitKatActivity extends AppCompatActivity {

    private static final String TAG = CreateFolderInSDCardAfterKitKatActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);

        final EditText folderPathEditText = (EditText) findViewById(R.id.createFolder_folderPathEditText);
        Button createButton = (Button) findViewById(R.id.createFolder_createButton);
        Button createFileInPackageFolderButton = (Button) findViewById(R.id.createFolder_createFileInPackageFolderButton);
        Button createFileInWrongWayButton = (Button) findViewById(R.id.createFolder_createFileInWrongWayButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolderInSDCard(CreateFolderInSDCardAfterKitKatActivity.this, folderPathEditText.getText().toString());
            }
        });
        createFileInPackageFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileInPackageFolder(CreateFolderInSDCardAfterKitKatActivity.this);
            }
        });
        createFileInWrongWayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileInWrongWay(CreateFolderInSDCardAfterKitKatActivity.this);
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
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(context, null);

        for (File external : externalStorageFiles) {

            String storageState = EnvironmentCompat.getStorageState(external);
            if (Environment.MEDIA_MOUNTED.equals(storageState)) {

                if (!external.exists()) {
                    if (external.mkdirs()) {
                        Log.i(TAG, "Create folder " + external.getAbsolutePath());
                    } else {
                        Log.e(TAG, "Can't create " + external.getAbsolutePath());
                        continue;
                    }
                }
                createFile(context, new File(external, "test.txt"));

            } else {
                logE(context, null, "Storage is not ready. (State = " + storageState + ")");
            }
        }
    }

    private void createFileInWrongWay(Context context) {
        List<String> mountPoints = getAllMountPoint(context);
        for (String mountPoint : mountPoints) {
            String packageFolderPath = createPackageFolderPath(context, mountPoint);
            File tmpFile = new File(packageFolderPath + File.separator + "test.txt");
            if (!tmpFile.getParentFile().exists()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    getExternalFilesDirs(null);
                } else {
                    tmpFile.getParentFile().mkdirs();
                }
            }

            try {
                if (tmpFile.createNewFile()) {
                    logI(context, "Successful to create file " + tmpFile.getAbsolutePath() + " in wrong way");
                } else {
                    logE(context, null, "Failed to create file " + tmpFile.getAbsolutePath());
                }
            } catch (IOException e) {
                logE(context, e, "Failed to create file " + tmpFile.getAbsolutePath());
            }
        }
    }

    private String createPackageFolderPath(Context context, String mountPoint) {
        if (mountPoint == null || mountPoint.isEmpty()) {
            return null;
        }
        final String PACKAGE_FOLDER_PATH = "Android" + File.separator + "data" + File.separator + context.getPackageName();
        if (!mountPoint.endsWith(File.separator)) {
            return mountPoint + File.separator + PACKAGE_FOLDER_PATH;
        } else {
            return mountPoint + PACKAGE_FOLDER_PATH;
        }
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

    private void createFile(Context context, @NonNull File file) {
        try {
            if (file.exists()) {
                Log.i(TAG, file.getAbsolutePath() + " already exists.");
                return;
            }

            if (file.createNewFile()) {
                logI(context, "Successful to create file " + file.getAbsolutePath());
            } else {
                logE(context, null, "Failed to create file " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            logE(context, e, "Failed to create file " + file.getAbsolutePath());
        }
    }

    private void logI(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.i(TAG, message);
    }

    private void logE(Context context, @Nullable Exception e, String message) {
        if (e != null) {
            e.printStackTrace();
        }
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
        Log.e(TAG, message);
    }

}
