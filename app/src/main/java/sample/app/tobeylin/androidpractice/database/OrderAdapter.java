package sample.app.tobeylin.androidpractice.database;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    private Cursor cursor;

    public void setCursor(Cursor newCursor) {
        if (cursor != null && cursor != newCursor) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return OrderViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(OrderViewHolder holder, int position) {
        holder.bindData(cursor, position);
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

}
