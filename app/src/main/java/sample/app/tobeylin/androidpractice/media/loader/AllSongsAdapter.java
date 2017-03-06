package sample.app.tobeylin.androidpractice.media.loader;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class AllSongsAdapter extends RecyclerView.Adapter<SongViewHolder> {

    private Cursor cursor;

    public AllSongsAdapter(Cursor cursor) {
        this.cursor = cursor;
    }

    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == cursor) {
            return null;
        }
        final Cursor oldCursor = cursor;
        cursor = newCursor;
        return oldCursor;
    }

    @Override
    public SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SongViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(SongViewHolder holder, int position) {
        holder.bindData(cursor, position);
    }

    @Override
    public long getItemId(int position) {
        if (cursor != null && cursor.moveToPosition(position)) {
            return cursor.getLong(0);
        }
        return 0;
    }

    @Override
    public int getItemCount() {
        if (cursor != null) {
            return cursor.getCount();
        }
        return 0;
    }

}
