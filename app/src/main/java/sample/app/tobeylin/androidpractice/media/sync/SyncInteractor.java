package sample.app.tobeylin.androidpractice.media.sync;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.widget.Toast;

import sample.app.tobeylin.androidpractice.media.sync.database.table.ActiveTrackTable;
import sample.app.tobeylin.androidpractice.media.sync.model.TrackType;

public class SyncInteractor {

    void startSync(Context context) {
        SyncAudioAsyncTask syncAudioAsyncTask = new SyncAudioAsyncTask(context);
        syncAudioAsyncTask.execute();
    }

    private static class SyncAudioAsyncTask extends AsyncTask<Void, Integer, Void> {

        private Context context;

        public SyncAudioAsyncTask(Context context) {
            this.context = context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            final Uri AUDIO_URI = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
            final String[] PROJECTION = new String[]{
                    MediaStore.Audio.Media.DATA
            };

            ContentResolver contentResolver = context.getContentResolver();
            Cursor cursor = contentResolver.query(AUDIO_URI, PROJECTION, null, null, null);

            if (cursor != null) {
                try {
                    cursor.moveToFirst();
                    final int DATA_INDEX = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
                    do {
                        String data = cursor.getString(DATA_INDEX);
                        ActiveTrackTable activeTrack = new ActiveTrackTable();
                        activeTrack.fileAbsolutePath = data;
                        activeTrack.type = TrackType.AUDIO;
                        activeTrack.save();
                    } while (cursor.moveToNext());
                } finally {
                    cursor.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(context, "Finish syncing", Toast.LENGTH_LONG).show();
        }

    }

    public interface SyncStateListener {

    }

}
