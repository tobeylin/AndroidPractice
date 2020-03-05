package sample.app.tobeylin.androidpractice.media.sync;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import sample.app.tobeylin.androidpractice.R;

public class SyncAllSongsActivity extends AppCompatActivity {

    private SyncInteractor syncInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_all_songs);

        syncInteractor = new SyncInteractor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItemCompat.setShowAsAction(menu.add("Start syncing"), MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        syncInteractor.startSync(this);
        return super.onOptionsItemSelected(item);
    }

}
