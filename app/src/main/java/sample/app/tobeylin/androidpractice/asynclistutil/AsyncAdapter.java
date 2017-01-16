package sample.app.tobeylin.androidpractice.asynclistutil;

import android.content.Context;
import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

class AsyncAdapter extends RecyclerView.Adapter<AsyncAdapter.TextViewHolder> {
    private AsyncListUtil<String> mAsyncListUtil;

    AsyncAdapter(List<String> textList, RecyclerView recyclerView, LinearLayoutManager linearLayoutManager) {
        mAsyncListUtil = new AsyncStringListUtil(textList, recyclerView, linearLayoutManager);
    }

    public void refresh() {
        mAsyncListUtil.refresh();
    }

    @Override
    public AsyncAdapter.TextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new AsyncAdapter.TextViewHolder(parent.getContext());
    }

    @Override
    public void onBindViewHolder(AsyncAdapter.TextViewHolder holder, int position) {
        final String itemString = mAsyncListUtil.getItem(position);
        if (itemString == null) {
            holder.textView.setText("loading...");
        } else {
            holder.textView.setText(itemString);
        }
    }

    @Override
    public int getItemCount() {
        return mAsyncListUtil.getItemCount();
    }

    static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(Context context) {
            super(new TextView(context));
            textView = (TextView) itemView;
        }
    }
}