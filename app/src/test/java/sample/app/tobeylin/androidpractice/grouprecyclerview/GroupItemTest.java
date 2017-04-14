package sample.app.tobeylin.androidpractice.grouprecyclerview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupItem;

import static org.junit.Assert.*;

public class GroupItemTest {

    @Test
    public void addChild() throws Exception {
        GroupItem<String> groupItem = new GroupItem<>("G1", "group_item_1");
        GroupChild<String> child1 = new GroupChild<>("C1");
        GroupChild<String> child2 = new GroupChild<>("C2");

        groupItem.addChild(child1);
        groupItem.addChild(child2);

        List<GroupChild<String>> expectedChildList = new ArrayList<>(2);
        expectedChildList.add(child1);
        expectedChildList.add(child2);
        assertEquals(expectedChildList.size(), groupItem.getChildCount());
        assertEquals(expectedChildList, groupItem.getChildList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChild_NullChild_ThrowException() throws Exception {
        GroupItem<String> groupItem = new GroupItem<>("group_item_1", "G1");
        groupItem.addChild(null);
    }

    @Test
    public void addChildAtGivenIndex() throws Exception {
        GroupItem<String> groupItem = new GroupItem<>("group_item_1", "G1");
        GroupChild<String> child1 = new GroupChild<>("C1");
        GroupChild<String> child2 = new GroupChild<>("C2");

        groupItem.addChild(0, child1);
        groupItem.addChild(0, child2);

        List<GroupChild<String>> expectedChildList = new ArrayList<>(2);
        expectedChildList.add(child2);
        expectedChildList.add(child1);
        assertEquals(expectedChildList.size(), groupItem.getChildCount());
        assertEquals(expectedChildList, groupItem.getChildList());
    }

    @Test(expected = IllegalArgumentException.class)
    public void addChildAtGivenIndex_NullChild_ThrowException() throws Exception {
        GroupItem<String> groupItem = new GroupItem<>("group_item_1", "G1");
        groupItem.addChild(0, null);
    }

}