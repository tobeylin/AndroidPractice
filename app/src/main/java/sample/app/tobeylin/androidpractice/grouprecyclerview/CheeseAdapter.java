package sample.app.tobeylin.androidpractice.grouprecyclerview;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupItem;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.ExpandableGroupListAdapter;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.LazyChildGroupItem;

public class CheeseAdapter extends ExpandableGroupListAdapter {

    private LazyChildGroupItem.Helper lazyHelper = new LazyChildGroupItem.Helper();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return SimpleTextViewHolder.newInstance(parent);
    }

    @Override
    public void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int flatPosition, GroupItem group) {
        if (group instanceof LazyChildGroupItem) {
            LazyChildGroupItem lazyChildGroup = (LazyChildGroupItem) group;
            if (lazyChildGroup.isCollapsed()) {

            }
        }
    }

    @Override
    public void onBindChildViewHolder(RecyclerView.ViewHolder holder, int flatPosition, int positionInGroup, GroupItem group, GroupChild child) {
        if (group instanceof LazyChildGroupItem) {
            LazyChildGroupItem lazyChildGroup = (LazyChildGroupItem) group;
            if (!lazyChildGroup.isLoaded()) {
                lazyHelper.asyncExpandGroupChild(lazyChildGroup, new LazyChildGroupItem.Helper.LoadGroupChildListener() {
                    @Override
                    public void onLoadGroupChildStart() {
                    }

                    @Override
                    public void onGroupChildLoaded(GroupItem group, int childIndexInGroup) {
                        notifyItemChanged(groupList.getGroupChildFlatPosition(group.getGroupId(), childIndexInGroup));
                    }

                    @Override
                    public void onLoadGroupChildComplete() {
                    }
                });

                SimpleText simpleText = new SimpleText("...", "...");
                ((SimpleTextViewHolder) holder).bindData(simpleText);
            } else {
                Object childData = child.getChildData();
                ((Cursor) childData).moveToPosition(positionInGroup);
                SimpleText simpleText = new SimpleText(((Cursor) childData).getString(0), ((Cursor) childData).getString(1));
                ((SimpleTextViewHolder) holder).bindData(simpleText);
            }
        }

//        if (holder instanceof SimpleTextViewHolder) {
//            Object childData = child.getChildData();
//            if (childData instanceof Cursor) {
//                ((Cursor) childData).moveToPosition(positionInGroup);
//                SimpleText simpleText = new SimpleText(((Cursor) childData).getString(0), ((Cursor) childData).getString(1));
//                ((SimpleTextViewHolder) holder).bindData(simpleText);
//            } else if (childData instanceof String) {
//                SimpleText simpleText = new SimpleText("...", "...");
//                ((SimpleTextViewHolder) holder).bindData(simpleText);
//            }
//        }
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

}
