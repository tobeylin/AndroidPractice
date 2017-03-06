package sample.app.tobeylin.androidpractice.storage.beforekitkat;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

import sample.app.tobeylin.androidpractice.R;

public class CreateFolderInSDCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_folder);

        final EditText folderPathEditText = (EditText) findViewById(R.id.createFolder_folderPathEditText);
        Button createButton = (Button) findViewById(R.id.createFolder_createButton);

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createFolderInSDCard(CreateFolderInSDCardActivity.this, folderPathEditText.getText().toString());
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

}
