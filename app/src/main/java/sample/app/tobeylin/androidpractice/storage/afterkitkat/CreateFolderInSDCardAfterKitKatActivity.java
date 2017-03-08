package sample.app.tobeylin.androidpractice.storage.afterkitkat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import sample.app.tobeylin.androidpractice.R;

public class CreateFolderInSDCardAfterKitKatActivity extends AppCompatActivity {

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
                createFolderInSDCard(CreateFolderInSDCardAfterKitKatActivity.this, folderPathEditText.getText().toString());
            }
        });
        createFileInPackageFolderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFileInPackageFolder(CreateFolderInSDCardAfterKitKatActivity.this);
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
        File[] externalStorageFiles = ContextCompat.getExternalFilesDirs(context, "AndroidPractice");
        for (File external : externalStorageFiles) {
            if (!external.exists()) {
                if (external.mkdirs()) {
                    Toast.makeText(context, "Successful to create folder " + external.getAbsolutePath(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Failed to create folder " + external.getAbsolutePath(), Toast.LENGTH_LONG).show();
                }
            } else {
                File tmpFile = new File(external.getAbsolutePath() + File.separator + "test.txt");
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
            }
        }
    }

}
