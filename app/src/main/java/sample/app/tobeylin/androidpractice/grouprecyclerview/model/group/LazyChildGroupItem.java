package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group;

import android.database.Cursor;
import android.os.AsyncTask;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.DatabaseRawQuery;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.expandable.ExpandableGroupItem;

public class LazyChildGroupItem<T> extends ExpandableGroupItem<T> {

    private final Object LOAD_LOCK = new Object();

    private DatabaseRawQuery childQuery;
    private int childCount;
    private boolean isLoaded = false;

    public LazyChildGroupItem(String groupId, T groupData, int childCount, DatabaseRawQuery childQuery) {
        super(groupId, groupData);
        this.childQuery = childQuery;
        this.childCount = childCount;
        expand();
    }

    public boolean isLoaded() {
        return isLoaded;
    }

    public void load(Helper.LoadGroupChildListener listener) {
        synchronized (LOAD_LOCK) {
            if (isLoaded) {
                return;
            }

            if (listener != null) {
                listener.onLoadGroupChildStart();
            }

            Cursor childCursor = childQuery.getQueryResult();
            int childCount = childCursor.getCount();
            for (int i = 0; i < childCount; ++i) {
                GroupChild<Cursor> child = new GroupChild<>(childCursor);
                replaceChild(i, child);
                if (listener != null) {
                    listener.onGroupChildLoaded(this, i);
                }
            }

            isLoaded = true;
            if (listener != null) {
                listener.onLoadGroupChildComplete();
            }
        }
    }

    @Override
    public void expand() {
        for (int i = 0; i < childCount; ++i) {
            GroupChild<String> emptyChild = new GroupChild<>("");
            addChild(emptyChild);
        }
        super.expand();
    }

    public static class Helper {

        public void asyncExpandGroupChild(LazyChildGroupItem lazyChildGroupItem, LoadGroupChildListener listener) {
            LoadGroupChildTask loadGroupChildTask = new LoadGroupChildTask(lazyChildGroupItem, listener);
            loadGroupChildTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        public interface LoadGroupChildListener {

            void onLoadGroupChildStart();

            void onGroupChildLoaded(GroupItem group, int childIndexInGroup);

            void onLoadGroupChildComplete();

        }

        private class LoadGroupChildTask extends AsyncTask<Void, Integer, Boolean> implements LoadGroupChildListener {

            private LazyChildGroupItem groupItem;
            private LoadGroupChildListener listener;

            LoadGroupChildTask(LazyChildGroupItem groupItem, LoadGroupChildListener listener) {
                this.groupItem = groupItem;
                this.listener = listener;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                listener.onLoadGroupChildStart();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                groupItem.load(this);
                return true;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                listener.onGroupChildLoaded(groupItem, values[0]);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                listener.onLoadGroupChildComplete();
            }

            @Override
            public void onLoadGroupChildStart() {}

            @Override
            public void onGroupChildLoaded(GroupItem group, int childIndexInGroup) {
                publishProgress(childIndexInGroup);
            }

            @Override
            public void onLoadGroupChildComplete() {}

        }

    }

}
