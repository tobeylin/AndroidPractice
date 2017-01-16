package sample.app.tobeylin.androidpractice.asynclistutil;

import android.support.v7.util.AsyncListUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.List;

class AsyncStringListUtil extends AsyncListUtil<String> {

    private static final int TILE_SIZE = 50;
    private static final long DELAY_MS = 500;

    AsyncStringListUtil(final List<String> textList, final RecyclerView recyclerView, final LinearLayoutManager linearLayoutManager) {
        super(String.class, TILE_SIZE,
                new AsyncListUtil.DataCallback<String>() {
                    @Override
                    public int refreshData() {
                        return textList.size();
                    }

                    @Override
                    public void fillData(String[] data, int startPosition, int itemCount) {
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
                    public void getItemRangeInto(int[] outRange) {
                        outRange[0] = linearLayoutManager.findFirstVisibleItemPosition();
                        outRange[1] = linearLayoutManager.findLastVisibleItemPosition();
                    }

                    @Override
                    public void onDataRefresh() {
                        recyclerView.getAdapter().notifyDataSetChanged();
                    }

                    @Override
                    public void onItemLoaded(int position) {
                        recyclerView.getAdapter().notifyItemChanged(position);
                    }
                });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                onRangeChanged();
            }
        });
    }
}