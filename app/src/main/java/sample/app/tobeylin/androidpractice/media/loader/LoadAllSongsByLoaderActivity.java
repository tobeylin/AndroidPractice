package sample.app.tobeylin.androidpractice.media.loader;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import sample.app.tobeylin.androidpractice.R;

public class LoadAllSongsByLoaderActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private final Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String[] PROJECTION = new String[]{
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA
    };
    private String SORT_ORDER = null;

    private ProgressBar loadingBar;
    private RecyclerView recyclerView;
    private AllSongsAdapter allSongsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_songs);

        loadingBar = (ProgressBar) findViewById(R.id.allSongs_loadingBar);
        recyclerView = (RecyclerView) findViewById(R.id.allSongs_recyclerView);

        allSongsAdapter = new AllSongsAdapter(null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(allSongsAdapter);
        loadingBar.setVisibility(View.VISIBLE);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, AUDIO_URI, PROJECTION, null, null, SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        loadingBar.setVisibility(View.INVISIBLE);
        allSongsAdapter.changeCursor(cursor);
        allSongsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader loader) {
        allSongsAdapter.swapCursor(null);
    }

}
