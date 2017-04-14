package sample.app.tobeylin.androidpractice.grouprecyclerview.model;

import android.database.Cursor;
import android.util.Log;

public class LazyChildGroupItem<T> extends GroupItem<T> {

    private DatabaseRawQuery childQuery;
    private boolean isLoadChild = false;

    public LazyChildGroupItem(String groupId, T groupData, int childCount, DatabaseRawQuery childQuery) {
        super(groupId, groupData);
        for (int i = 0; i < childCount; ++i) {
            GroupChild<String> emptyChild = new GroupChild<>("");
            addChild(emptyChild);
        }
        this.childQuery = childQuery;
    }

    public boolean isLoadChild() {
        return isLoadChild;
    }

    public void loadChild() {
        loadChild(null);
    }

    public void loadChild(LoadGroupChildListener listener) {
        isLoadChild = true;
        Log.i("test", "start load child");
        Cursor childCursor = childQuery.getQueryResult();
        int childCount = childCursor.getCount();
        for (int i = 0; i < childCount; ++i) {
            GroupChild<Cursor> child = new GroupChild<>(childCursor);
            replaceChild(i, child);
            listener.onGroupChildLoaded(this, i);
        }
    }

    public interface LoadGroupChildListener {
        void onGroupChildLoaded(GroupItem group, int childIndexInGroup);
    }

}
