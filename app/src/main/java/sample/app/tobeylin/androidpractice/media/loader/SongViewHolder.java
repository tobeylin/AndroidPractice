package sample.app.tobeylin.androidpractice.media.loader;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sample.app.tobeylin.androidpractice.R;

class SongViewHolder extends RecyclerView.ViewHolder {

    private TextView songTitle;
    private TextView songPath;

    static SongViewHolder newInstance(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new SongViewHolder(inflater.inflate(R.layout.item_song, parent, false));
    }

    private SongViewHolder(View itemView) {
        super(itemView);

        songTitle = (TextView) itemView.findViewById(R.id.song_title);
        songPath = (TextView) itemView.findViewById(R.id.song_path);
    }

    void bindData(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        songTitle.setText(cursor.getString(1));
        songPath.setText(cursor.getString(2));
    }

}
