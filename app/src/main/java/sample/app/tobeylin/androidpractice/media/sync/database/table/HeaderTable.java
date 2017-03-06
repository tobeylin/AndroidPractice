package sample.app.tobeylin.androidpractice.media.sync.database.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import sample.app.tobeylin.androidpractice.media.sync.database.TrackDatabase;

@Table(database = TrackDatabase.class, name = "header")
public class HeaderTable extends BaseModel {

    @Column(name = "_id")
    @PrimaryKey
    long id;

    @Column(name = "header_text")
    @NotNull
    String headerText;

}
