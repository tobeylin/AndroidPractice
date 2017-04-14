package sample.app.tobeylin.androidpractice.grouprecyclerview;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupChild;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupItem;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.GroupList;

import static org.junit.Assert.*;

public class GroupListTest {

    @Test
    public void getGroupCount() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(3, groupList.getGroupCount());
    }

    @Test
    public void getChildCount() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(5, groupList.getChildCount());
    }

    @Test
    public void getGroupItem_HasSameGroupId_ReturnFirstGroupHasSameId() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        GroupItem actualGroup = groupList.getGroupItem("G1");
        assertNotNull(actualGroup);
        assertEquals(group1, actualGroup);
    }

    @Test
    public void getGroupItem_NoSameGroupId_ReturnNull() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        GroupItem actualGroup = groupList.getGroupItem("G3");
        assertNull(actualGroup);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGroupItem_NullGroupId_ThrowException() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        groupList.getGroupItem(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getGroupItem_EmptyGroupId_ThrowException() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        groupList.getGroupItem("");
    }

    @Test
    public void getChildListByGroupId_HasGroupAndChild_ReturnGroupChild() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        List<GroupChild> actualChildList = groupList.getChildList("G1");

        assertNotNull(actualChildList);
        List<GroupChild<String>> expectedChildList = new ArrayList<>(2);
        expectedChildList.add(child1_1);
        expectedChildList.add(child1_2);
        assertEquals(expectedChildList.size(), actualChildList.size());
        assertEquals(expectedChildList, actualChildList);
    }

    @Test
    public void getChildListByGroupId_NoGroup_ReturnNull() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        List<GroupChild> actualChildList = groupList.getChildList("G3");

        assertNull(actualChildList);
    }

    @Test
    public void getChildList_HasGroupAndChild_ReturnAllChild() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        List<GroupChild> actualChildList = groupList.getChildList();

        assertNotNull(actualChildList);
        List<GroupChild<String>> expectedChildList = new ArrayList<>(2);
        expectedChildList.add(child1_1);
        expectedChildList.add(child1_2);
        expectedChildList.add(child2_1);
        expectedChildList.add(child2_2);
        expectedChildList.add(child3_1);
        assertEquals(expectedChildList.size(), actualChildList.size());
        assertEquals(expectedChildList, actualChildList);
    }

    @Test
    public void getGroupIndex() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(0, groupList.getGroupIndex(0));
        assertEquals(0, groupList.getGroupIndex(1));
        assertEquals(1, groupList.getGroupIndex(2));
        assertEquals(1, groupList.getGroupIndex(3));
        assertEquals(2, groupList.getGroupIndex(4));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getGroupIndex_InvalidIndex_ThrowException() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(0, groupList.getGroupIndex(10));
    }

    @Test
    public void getGroupChildFlatPosition() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(3, groupList.getGroupChildFlatPosition("G2", 1));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getGroupChildFlatPosition_InvalidChildIndex_ThrowException() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        groupList.getGroupChildFlatPosition("G2", 2);
    }

    @Test(expected = RuntimeException.class)
    public void getGroupChildFlatPosition_NoSuchGroup_ThrowException() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G1", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        groupList.getGroupChildFlatPosition("G4", 0);
    }

    @Test
    public void getChildIndexInGroup() throws Exception {
        final GroupList groupList = new GroupList();
        final GroupItem<String> group1 = new GroupItem<>("G1", "group_item_1");
        final GroupItem<String> group2 = new GroupItem<>("G2", "group_item_2");
        final GroupItem<String> group3 = new GroupItem<>("G3", "group_item_3");
        final GroupChild<String> child1_1 = new GroupChild<>("C1-1");
        final GroupChild<String> child1_2 = new GroupChild<>("C1-2");
        final GroupChild<String> child2_1 = new GroupChild<>("C2-1");
        final GroupChild<String> child2_2 = new GroupChild<>("C2-2");
        final GroupChild<String> child3_1 = new GroupChild<>("C3-1");
        group1.addChild(child1_1);
        group1.addChild(child1_2);
        group2.addChild(child2_1);
        group2.addChild(child2_2);
        group3.addChild(child3_1);
        groupList.add(group1);
        groupList.add(group2);
        groupList.add(group3);

        assertEquals(1, groupList.getChildIndexInGroup("G2", 3));
        assertEquals(0, groupList.getChildIndexInGroup("G3", 4));
    }

}