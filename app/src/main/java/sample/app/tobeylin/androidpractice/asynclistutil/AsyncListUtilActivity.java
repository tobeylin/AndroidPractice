package sample.app.tobeylin.androidpractice.asynclistutil;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.AsyncListUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;

public class AsyncListUtilActivity extends AppCompatActivity {

    private static final String TAG = AsyncListUtilActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private List<String> textList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecyclerView = new RecyclerView(this);
        mLinearLayoutManager = new LinearLayoutManager(this);
        textList.addAll(Arrays.asList(Cheeses.sCheeseStrings));
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        final ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mRecyclerView.setLayoutParams(layoutParams);
        mRecyclerView.setAdapter(new AsyncAdapter(textList));
        setContentView(mRecyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItem layoutItem = menu.add("Show the last 6 items");
        layoutItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<String> tmpList = new ArrayList<>(6);
        for (int i = textList.size() - 1; i >= textList.size() - 6; --i) {
            tmpList.add(textList.get(i));
        }
        textList.clear();
        textList.addAll(tmpList);
        AsyncAdapter adapter = (AsyncAdapter) mRecyclerView.getAdapter();
        if (adapter != null) {
            adapter.refresh();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private static class TextViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        TextViewHolder(Context context) {
            super(new TextView(context));
            textView = (TextView) itemView;
        }
    }

    private class AsyncAdapter extends RecyclerView.Adapter<TextViewHolder> {
        private AsyncListUtil<String> mAsyncListUtil;

        AsyncAdapter(final List<String> textList) {
            mAsyncListUtil = new AsyncStringListUtil(textList);
        }

        @NonNull
        @Override
        public TextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TextViewHolder(parent.getContext());
        }

        @Override
        public void onBindViewHolder(@NonNull TextViewHolder holder, int position) {
            final String itemString = mAsyncListUtil.getItem(position);
            if (itemString == null) {
                holder.textView.setText(R.string.async_list_util_loading);
            } else {
                holder.textView.setText(itemString);
            }
        }

        @Override
        public int getItemCount() {
            return mAsyncListUtil.getItemCount();
        }

        void refresh() {
            mAsyncListUtil.refresh();
        }
    }

    private class AsyncStringListUtil extends AsyncListUtil<String> {

        /*
        Problem: If the tile size is 50, the AsyncListUtil doesn't clear the cache
        after refresh(). Recycle view shows the top 6 items in the old list,
        but it should show the last 6 items. It's okay when the tile size
        is 5.
        */
        private static final int TILE_SIZE = 50;
        private static final long DELAY_MS = 500;

        AsyncStringListUtil(final List<String> textList) {
            super(String.class, TILE_SIZE,
                    new AsyncListUtil.DataCallback<String>() {
                        @Override
                        public int refreshData() {
                            return textList.size();
                        }

                        @Override
                        public void fillData(@NonNull String[] data, int startPosition, int itemCount) {
                            sleep();
                            for (int i = 0; i < itemCount; i++) {
                                data[i] = textList.get(startPosition + i);
                            }
                        }

                        private void sleep() {
                            try {
                                Thread.sleep(DELAY_MS);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new AsyncListUtil.ViewCallback() {
                        @Override
                        public void getItemRangeInto(@NonNull int[] outRange) {
                            outRange[0] = mLinearLayoutManager.findFirstVisibleItemPosition();
                            outRange[1] = mLinearLayoutManager.findLastVisibleItemPosition();
                        }

                        @Override
                        public void onDataRefresh() {
                            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                        }

                        @Override
                        public void onItemLoaded(int position) {
                            RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
                            if (adapter != null) {
                                adapter.notifyItemChanged(position);
                            }
                        }
                    });
            mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    onRangeChanged();
                }
            });
        }
    }

}