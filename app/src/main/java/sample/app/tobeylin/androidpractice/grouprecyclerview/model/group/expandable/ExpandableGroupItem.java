package sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.expandable;

import java.util.Collections;
import java.util.List;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.GroupItem;

public class ExpandableGroupItem<T> extends GroupItem<T> implements ExpandableItem {

    private boolean isExpanded;

    public ExpandableGroupItem(String groupId, T groupData) {
        super(groupId, groupData);
    }

    public ExpandableGroupItem(String groupId, T groupData, List<GroupChild> childList) {
        super(groupId, groupData, childList);
    }

    public List<GroupChild> getList() {
        if (isExpanded()) {
            return getChildList();
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public boolean isExpanded() {
        return isExpanded;
    }

    @Override
    public boolean isCollapsed() {
        return !isExpanded;
    }

    @Override
    public void expand() {
        isExpanded = true;
    }

    @Override
    public void collapse() {
        isExpanded = false;
    }

}
