package sample.app.tobeylin.androidpractice.grouprecyclerview;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.database.AndroidDatabase;

import java.util.ArrayList;
import java.util.List;

import sample.app.tobeylin.androidpractice.R;
import sample.app.tobeylin.androidpractice.grouprecyclerview.database.GroupItemCursor;
import sample.app.tobeylin.androidpractice.grouprecyclerview.database.SimpleTextDatabase;
import sample.app.tobeylin.androidpractice.grouprecyclerview.database.SimpleTextRecord;
import sample.app.tobeylin.androidpractice.grouprecyclerview.database.SimpleTextRecord_Table;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.Cheeses;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.query.DatabaseRawQuery;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.LazyChildGroupItem;
import sample.app.tobeylin.androidpractice.grouprecyclerview.model.group.expandable.ExpandableGroupList;

public class GroupRecyclerViewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_recycler_view);

        recyclerView = (RecyclerView) findViewById(R.id.groupRecyclerView_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        CheeseAdapter cheeseAdapter = new CheeseAdapter();
        cheeseAdapter.setGroupList(getGroupList());
        recyclerView.setAdapter(cheeseAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuItemCompat.setShowAsAction(menu.add("Add"), MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        List<SimpleTextRecord> simpleTextRecords = new ArrayList<>(Cheeses.sCheeseStrings.length);
        for (String text : Cheeses.sCheeseStrings) {
            SimpleTextRecord simpleTextRecord = new SimpleTextRecord();
            simpleTextRecord.headerText = text.substring(0, 1);
            simpleTextRecord.simpleText = text;
            simpleTextRecords.add(simpleTextRecord);
        }
        FlowManager.getModelAdapter(SimpleTextRecord.class).insertAll(simpleTextRecords);
        return super.onOptionsItemSelected(item);
    }

    private ExpandableGroupList getGroupList() {
        Cursor cursor = SQLite.select(SimpleTextRecord_Table.header_text, Method.count(SimpleTextRecord_Table._id))
                .from(SimpleTextRecord.class)
                .groupBy(SimpleTextRecord_Table.header_text)
                .query();
        GroupItemCursor groupItemCursor = new GroupItemCursor(cursor, 0, 1);
        return toGroupList(groupItemCursor);
    }

    private ExpandableGroupList toGroupList(GroupItemCursor groupItemCursor) {
        ExpandableGroupList<LazyChildGroupItem> groupList = new ExpandableGroupList<>();
        groupItemCursor.moveToFirst();
        do {
            Header header = new Header(groupItemCursor.getGroupTitle());
            SQLiteDatabase database = ((AndroidDatabase) FlowManager.getDatabase(SimpleTextDatabase.DB_NAME).getWritableDatabase()).getDatabase();
            String childQuery = SQLite.select(SimpleTextRecord_Table.header_text, SimpleTextRecord_Table.simple_text)
                    .from(SimpleTextRecord.class)
                    .where(SimpleTextRecord_Table.header_text.eq(header.title))
                    .orderBy(SimpleTextRecord_Table.simple_text, true)
                    .getQuery();
            LazyChildGroupItem<Header> groupItem = new LazyChildGroupItem<>(
                    String.valueOf(groupItemCursor.getGroupTitle().hashCode()),
                    header,
                    groupItemCursor.getChildCount(),
                    new DatabaseRawQuery(database, childQuery)
            );
            groupList.add(groupItem);
        } while(groupItemCursor.moveToNext());
        return groupList;
    }

}
