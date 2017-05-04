package sample.app.tobeylin.androidpractice.media.trackinfo;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import sample.app.tobeylin.androidpractice.R;

public class TrackInfoActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = TrackInfoActivity.class.getSimpleName();
    private final Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String[] PROJECTION = new String[]{
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_KEY,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_KEY
    };

    private TextView trackNameTextView;
    private TextView trackPathTextView;
    private TextView albumNameTextView;
    private TextView albumKeyTextView;
    private TextView artistNameTextView;
    private TextView artistKeyTextView;
    private String keyword = "Feelings";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_info);

        trackNameTextView = (TextView) findViewById(R.id.trackInfo_trackName);
        trackPathTextView = (TextView) findViewById(R.id.trackInfo_trackPath);
        albumNameTextView = (TextView) findViewById(R.id.trackInfo_albumName);
        albumKeyTextView = (TextView) findViewById(R.id.trackInfo_albumKey);
        artistNameTextView = (TextView) findViewById(R.id.trackInfo_artistName);
        artistKeyTextView = (TextView) findViewById(R.id.trackInfo_artistKey);

        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String selection = MediaStore.Audio.Media.TITLE + " like '%" + keyword + "%'";
        return new CursorLoader(this, AUDIO_URI, PROJECTION, selection, null, null);
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() == 0) {
            Toast.makeText(this, "No Result", Toast.LENGTH_LONG).show();
            return;
        }

        cursor.moveToFirst();
        trackNameTextView.setText(cursor.getString(0));
        trackPathTextView.setText(cursor.getString(1));
        albumNameTextView.setText(cursor.getString(2));
        albumKeyTextView.setText(cursor.getString(3));
        artistNameTextView.setText(cursor.getString(4));
        artistKeyTextView.setText(cursor.getString(5));

        Log.i(TAG, "Track Name = " + cursor.getString(0));
        Log.i(TAG, "Track Path = " + cursor.getString(1));
        Log.i(TAG, "Album Name = " + cursor.getString(2));
        Log.i(TAG, "Album Key = " + cursor.getString(3));
        Log.i(TAG, "Artist Name = " + cursor.getString(4));
        Log.i(TAG, "Artist Key = " + cursor.getString(5));
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

}
