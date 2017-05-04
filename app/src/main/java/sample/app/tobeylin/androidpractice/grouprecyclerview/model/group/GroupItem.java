package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group;

import java.util.ArrayList;
import java.util.List;

public class GroupItem<T> {

    private String groupId;
    private T groupData;
    private List<GroupChild> childList;

    public GroupItem(String groupId, T groupData) {
        this(groupId, groupData, new ArrayList<GroupChild>());
    }

    public GroupItem(String groupId, T groupData, List<GroupChild> childList) {
        this.groupId = groupId;
        this.groupData = groupData;
        this.childList = childList;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public T getGroupData() {
        return groupData;
    }

    public void setGroupData(T groupData) {
        this.groupData = groupData;
    }

    public List<GroupChild> getChildList() {
        return childList;
    }

    public int getChildCount() {
        return (childList == null) ? 0 : childList.size();
    }

    public void addChild(GroupChild child) {
        if (child == null) {
            throw new IllegalArgumentException("child should be a non null object");
        }

        if (childList == null) {
            childList = new ArrayList<>();
        }

        childList.add(child);
    }

    public void addChild(int index, GroupChild child) {
        if (child == null) {
            throw new IllegalArgumentException("child should be a non null object");
        }

        if (childList == null) {
            childList = new ArrayList<>();
        }

        childList.add(index, child);
    }

    public void replaceChild(int index, GroupChild child) {
        childList.set(index, child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupItem<?> groupItem = (GroupItem<?>) o;

        if (groupId != null ? !groupId.equals(groupItem.groupId) : groupItem.groupId != null)
            return false;
        if (groupData != null ? !groupData.equals(groupItem.groupData) : groupItem.groupData != null)
            return false;
        return childList != null ? childList.equals(groupItem.childList) : groupItem.childList == null;

    }

    @Override
    public int hashCode() {
        int result = groupId != null ? groupId.hashCode() : 0;
        result = 31 * result + (groupData != null ? groupData.hashCode() : 0);
        result = 31 * result + (childList != null ? childList.hashCode() : 0);
        return result;
    }
}
