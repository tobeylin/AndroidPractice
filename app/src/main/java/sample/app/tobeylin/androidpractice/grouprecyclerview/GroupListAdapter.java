package sample.app.tobeylin.androidpractice.grouprecyclerview;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupItem;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupList;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.LazyChildGroupItem;

public class GroupListAdapter extends RecyclerView.Adapter {

    private GroupList groupList;

    public void setGroupList(GroupList groupList) {
        this.groupList = groupList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SimpleTextViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final int groupIndex = groupList.getGroupIndex(position);
        final LazyChildGroupItem groupItem = (LazyChildGroupItem) groupList.getGroupItem(groupIndex);

        if (!groupItem.isLoadChild()) {
            LoadGroupChildTask loadGroupChildTask = new LoadGroupChildTask(groupItem) {
                @Override
                protected void onProgressUpdate(Integer... values) {
                    notifyItemChanged(groupList.getGroupChildFlatPosition(getGroupId(), values[0]));
                }
            };
            loadGroupChildTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        GroupChild child = getChild(position);
        if (holder instanceof SimpleTextViewHolder) {
            Object childData = child.getChildData();
            if (childData instanceof Cursor) {
                ((Cursor) childData).moveToPosition(groupList.getChildIndexInGroup(groupItem.getGroupId(), position));
                SimpleText simpleText = new SimpleText(((Cursor) childData).getString(0), ((Cursor) childData).getString(1));
                ((SimpleTextViewHolder) holder).bindData(simpleText);
            } else if (childData instanceof String) {
                SimpleText simpleText = new SimpleText("...", "...");
                ((SimpleTextViewHolder) holder).bindData(simpleText);
            }
        }

    }

    @Override
    public int getItemCount() {
        return groupList.getChildCount();
    }

    private GroupChild getChild(int position) {
        try {
            return groupList.getChildList().get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static class SimpleTextViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView subTitle;

        public static SimpleTextViewHolder newInstance(ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            return new SimpleTextViewHolder(inflater.inflate(R.layout.item_simple_text, parent, false));
        }

        SimpleTextViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.simpleText_titleTextView);
            subTitle = (TextView) itemView.findViewById(R.id.simpleText_subTitleTextView);
        }

        void bindData(SimpleText simpleText) {
            title.setText(simpleText.title);
            subTitle.setText(simpleText.subTitle);
        }

    }

    private class LoadGroupChildTask extends AsyncTask<Void, Integer, Boolean> implements LazyChildGroupItem.LoadGroupChildListener {

        private LazyChildGroupItem groupItem;

        LoadGroupChildTask(LazyChildGroupItem groupItem) {
            this.groupItem = groupItem;
        }

        String getGroupId() {
            return groupItem.getGroupId();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            groupItem.loadChild(this);
            return true;
        }

        @Override
        public void onGroupChildLoaded(GroupItem group, int childIndexInGroup) {
            publishProgress(childIndexInGroup);
        }
    }

}
