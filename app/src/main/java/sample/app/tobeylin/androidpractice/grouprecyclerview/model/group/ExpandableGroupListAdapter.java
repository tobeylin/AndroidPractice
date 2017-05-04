package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.expandable.ExpandableGroupList;

public abstract class ExpandableGroupListAdapter extends RecyclerView.Adapter {

    protected ExpandableGroupList<? extends GroupItem> groupList;

    public abstract void onBindGroupViewHolder(RecyclerView.ViewHolder holder, int flatPosition, GroupItem group);
    public abstract void onBindChildViewHolder(RecyclerView.ViewHolder holder, int flatPosition, int positionInGroup, GroupItem group, GroupChild child);

    public void setGroupList(ExpandableGroupList<? extends GroupItem> groupList) {
        this.groupList = groupList;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int flatPosition) {
        List resultList = groupList.getList();
        Object item = resultList.get(flatPosition);
        if (item instanceof GroupChild) {
            final int groupIndex = groupList.getGroupIndex(flatPosition);
            final GroupItem groupItem = groupList.getGroupItem(groupIndex);
            final int positionInGroup = groupList.getChildIndexInGroup(groupItem.getGroupId(), flatPosition);
            onBindChildViewHolder(holder, flatPosition, positionInGroup, groupItem, getChild(flatPosition));
        } else {
            final int groupIndex = groupList.getGroupIndex(flatPosition);
            final GroupItem groupItem = groupList.getGroupItem(groupIndex);
            onBindGroupViewHolder(holder, flatPosition, groupItem);
        }
    }

    @Override
    public int getItemCount() {
        return groupList.getList().size();
    }

    private GroupChild getChild(int position) {
        try {
            return (GroupChild) groupList.getList().get(position);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

}
