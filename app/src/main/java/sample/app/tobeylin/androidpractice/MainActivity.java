package sample.app.tobeylin.androidpractice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import sample.app.tobeylin.androidpractice.asynclistutil.AsyncListUtilActivity;
import sample.app.tobeylin.androidpractice.handleractivityleak.HandlerLeakActivity;
import sample.app.tobeylin.androidpractice.media.loader.LoadAllSongsByLoaderActivity;
import sample.app.tobeylin.androidpractice.media.sync.SyncAllSongsActivity;
import sample.app.tobeylin.androidpractice.storage.afterkitkat.CreateFolderInSDCardAfterKitKatActivity;
import sample.app.tobeylin.androidpractice.storage.afterkitkat.MountPointAfterKitKatActivity;
import sample.app.tobeylin.androidpractice.storage.beforekitkat.CreateFolderInSDCardActivity;
import sample.app.tobeylin.androidpractice.storage.beforekitkat.MountPointBeforeKitKatActivity;
import sample.app.tobeylin.androidpractice.viewpager.ViewPagerActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button asyncListUtilSampleButton = (Button) findViewById(R.id.main_asyncListUtilButton);
        asyncListUtilSampleButton.setOnClickListener(this);

        Button handlerActivityLeakButton = (Button) findViewById(R.id.main_handlerActivityLeakButton);
        handlerActivityLeakButton.setOnClickListener(this);

        Button viewPagerActivityButton = (Button) findViewById(R.id.main_viewPagerActivityButton);
        viewPagerActivityButton.setOnClickListener(this);

        Button allSongsActivityButton = (Button) findViewById(R.id.main_loadAllSongsByLoaderActivityButton);
        allSongsActivityButton.setOnClickListener(this);

        Button syncAllSongsActivityButton = (Button) findViewById(R.id.main_syncAllSongsActivityButton);
        syncAllSongsActivityButton.setOnClickListener(this);

        Button mountPointAfterKitKatActivityButton = (Button) findViewById(R.id.main_mountPointAfterKitKatActivityButton);
        mountPointAfterKitKatActivityButton.setOnClickListener(this);

        Button mountPointBeforeKitKatActivityButton = (Button) findViewById(R.id.main_mountPointBeforeKitKatActivityButton);
        mountPointBeforeKitKatActivityButton.setOnClickListener(this);

        Button createFolderInSDCardActivityButton = (Button) findViewById(R.id.main_createFolderInSDCardActivityButton);
        createFolderInSDCardActivityButton.setOnClickListener(this);

        Button createFolderInSDCardAfterKitKatActivityButton = (Button) findViewById(R.id.main_createFolderInSDCardAfterKitKatActivityButton);
        createFolderInSDCardAfterKitKatActivityButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.main_asyncListUtilButton:
                startActivity(new Intent().setClass(getApplicationContext(), AsyncListUtilActivity.class));
                break;
            case R.id.main_handlerActivityLeakButton:
                startActivity(new Intent().setClass(getApplicationContext(), HandlerLeakActivity.class));
                break;
            case R.id.main_viewPagerActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), ViewPagerActivity.class));
                break;
            case R.id.main_loadAllSongsByLoaderActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), LoadAllSongsByLoaderActivity.class));
                break;
            case R.id.main_syncAllSongsActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), SyncAllSongsActivity.class));
                break;
            case R.id.main_mountPointAfterKitKatActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), MountPointAfterKitKatActivity.class));
                break;
            case R.id.main_mountPointBeforeKitKatActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), MountPointBeforeKitKatActivity.class));
                break;
            case R.id.main_createFolderInSDCardActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), CreateFolderInSDCardActivity.class));
                break;
            case R.id.main_createFolderInSDCardAfterKitKatActivityButton:
                startActivity(new Intent().setClass(getApplicationContext(), CreateFolderInSDCardAfterKitKatActivity.class));
                break;
            default:
        }
    }
}
