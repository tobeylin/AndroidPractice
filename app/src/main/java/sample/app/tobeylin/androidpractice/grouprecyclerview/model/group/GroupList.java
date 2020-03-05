package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class GroupList<T extends GroupItem> extends ArrayList<T> {

    private static final long serialVersionUID = 9088342100991009332L;

    public int getGroupCount() {
        return size();
    }

    public int getChildCount() {
        int childCount = 0;
        for (GroupItem groupItem : this) {
            if (groupItem != null) {
                childCount = childCount + groupItem.getChildCount();
            }
        }
        return childCount;
    }

    public T getGroupItem(int groupIndex) {
        return get(groupIndex);
    }

    public T getGroupItem(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new IllegalArgumentException("groupId should be a non empty string");
        }

        for (T groupItem : this) {
            if (groupItem.getGroupId().equals(groupId)) {
                return groupItem;
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<GroupChild> getChildList(String groupId) {
        GroupItem groupItem = getGroupItem(groupId);
        if (groupItem != null) {
            return groupItem.getChildList();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    public List<GroupChild> getChildList() {
        List<GroupChild> childList = new ArrayList<>();
        for (GroupItem groupItem : this) {
            childList.addAll(groupItem.getChildList());
        }
        return childList;
    }

    public int getGroupIndex(int childFlatPosition) {
        int childCount = getChildCount();
        if (childFlatPosition < 0 || childFlatPosition >= childCount) {
            throw new IndexOutOfBoundsException("childPosition: " + childFlatPosition + ",Size: " + childCount);
        }

        int groupCount = size();
        int accumulatePosition = -1;

        for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
            GroupItem groupItem = get(groupIndex);
            int groupItemChildCount = groupItem.getChildCount();
            accumulatePosition += groupItemChildCount;
            if (childFlatPosition <= accumulatePosition) {
                return groupIndex;
            }
        }
        throw new RuntimeException("Unknown state");
    }

    public int getGroupChildFlatPosition(String groupId, int childIndexInGroup) {
        int flatPosition = -1;

        for (GroupItem group : this) {
            if (group.getGroupId().equals(groupId)) {
                final int childCount = group.getChildCount();
                if (childIndexInGroup < 0 || childIndexInGroup >= childCount) {
                    throw new IndexOutOfBoundsException("index: " + childIndexInGroup + ", Size: " + childCount);
                }

                return flatPosition + childIndexInGroup + 1;
            } else {
                flatPosition += group.getChildCount();
            }
        }

        throw new RuntimeException("No such group has id: " + groupId);
    }

    public int getChildIndexInGroup(final String groupId, final int childFlatPosition) {
        int childIndexInGroup = childFlatPosition;
        for (GroupItem group : this) {
            if (group.getGroupId().equals(groupId)) {
                return childIndexInGroup;
            } else {
                childIndexInGroup -= group.getChildCount();
            }
        }
        throw new RuntimeException("No such group has id: " + groupId);
    }

}
