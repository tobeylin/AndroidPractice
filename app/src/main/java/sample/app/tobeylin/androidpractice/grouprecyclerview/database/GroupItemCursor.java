package sample.app.tobeylin.androidpractice.grouprecyclerview.database;

import android.database.Cursor;
import android.database.CursorWrapper;

public class GroupItemCursor extends CursorWrapper {

    private final int groupTitleIndex;
    private final int childCountIndex;

    public GroupItemCursor(Cursor cursor, int groupTitleIndex, int childCountIndex) {
        super(cursor);
        this.groupTitleIndex = groupTitleIndex;
        this.childCountIndex = childCountIndex;
    }

    public String getGroupTitle() {
        return getString(groupTitleIndex);
    }

    public int getChildCount() {
        return getInt(childCountIndex);
    }

}
