package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.expandable;

import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupItem;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupList;

public class ExpandableGroupList<T extends ExpandableGroupItem> extends GroupList<T> {

    private static final long serialVersionUID = 5163847618683192383L;
    private static final int FILTER_ALL = 1;
    private static final int FILTER_ONLY_GROUP = 2;
    private static final int FILTER_ONLY_CHILD = 3;

    private int filter = FILTER_ALL;

    public void setFilter(int filter) {
        this.filter = filter;
    }

    public List getList() {
        List<Object> resultList = new ArrayList<>();
        for (ExpandableGroupItem groupItem : this) {
            if (groupItem.isCollapsed()) {
                if (filter == FILTER_ALL || filter == FILTER_ONLY_GROUP) {
                    resultList.add(groupItem);
                }
            } else {
                if (filter == FILTER_ALL || filter == FILTER_ONLY_CHILD) {
                    resultList.addAll(groupItem.getChildList());
                }
            }
        }
        return resultList;
    }

    public boolean isExpandedGroup(String groupId) {
        return getGroupItem(groupId).isExpanded();
    }

    public void expandGroup(String groupId) {
        ExpandableGroupItem groupItem = getGroupItem(groupId);
        if (groupItem.isCollapsed()) {
            groupItem.expand();
        }
    }

    public void expandAll() {
        for (ExpandableGroupItem groupItem : this) {
            if (groupItem.isCollapsed()) {
                groupItem.expand();
            }
        }
    }

    public void collapseGroup(String groupId) {

    }

    public void collapseAll() {

    }

    @Override
    public int getGroupIndex(int expandedItemFlatPosition) {
        int expandedItemCount = getList().size();
        if (expandedItemFlatPosition < 0 || expandedItemFlatPosition >= expandedItemCount) {
            throw new IndexOutOfBoundsException("childPosition: " + expandedItemFlatPosition + ",Size: " + expandedItemCount);
        }

        Object item = getList().get(expandedItemFlatPosition);
        if (item instanceof GroupItem) {
            return indexOf(item);
        } else if (item instanceof GroupChild) {

            int groupCount = size();
            int accumulatePosition = -1;

            for (int groupIndex = 0; groupIndex < groupCount; groupIndex++) {
                ExpandableGroupItem groupItem = get(groupIndex);
                if (groupItem.isExpanded()) {
                    int groupItemChildCount = groupItem.getChildCount();
                    accumulatePosition += groupItemChildCount;
                } else {
                    if (filter == FILTER_ALL) {
                        accumulatePosition += 1;
                    }
                }
                if (expandedItemFlatPosition <= accumulatePosition) {
                    return groupIndex;
                }
            }
            throw new RuntimeException("Unknown state");
        }

        throw new RuntimeException("Unknown state");
    }

}
