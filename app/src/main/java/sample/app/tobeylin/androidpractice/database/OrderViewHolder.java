package sample.app.tobeylin.androidpractice.database;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import sample.app.tobeylin.androidpractice.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    private TextView orderIdTextView;
    private TextView customerNameTextView;
    private TextView itemTextView;

    public static OrderViewHolder newInstance(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new OrderViewHolder(inflater.inflate(R.layout.item_order, parent, false));
    }

    public OrderViewHolder(View itemView) {
        super(itemView);

        orderIdTextView = (TextView) itemView.findViewById(R.id.order_id);
        customerNameTextView = (TextView) itemView.findViewById(R.id.order_customerName);
        itemTextView = (TextView) itemView.findViewById(R.id.order_item);
    }

    void bindData(Cursor cursor, int position) {
        cursor.moveToPosition(position);
        orderIdTextView.setText(itemView.getContext().getResources().getString(R.string.order_id, cursor.getLong(0)));
        customerNameTextView.setText(itemView.getContext().getResources().getString(R.string.order_customer_name, cursor.getString(1)));
        itemTextView.setText(itemView.getContext().getResources().getString(R.string.order_item, cursor.getString(2)));
    }

}
