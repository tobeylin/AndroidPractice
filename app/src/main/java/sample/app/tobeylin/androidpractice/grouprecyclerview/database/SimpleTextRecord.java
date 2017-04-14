package sample.app.tobeylin.androidpractice.grouprecyclerview.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = SimpleTextDatabase.class, name = "simple_text")
public class SimpleTextRecord extends BaseModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column(name = "header_text")
    @NotNull
    public String headerText;

    @Column(name = "simple_text")
    @NotNull
    public String simpleText;

}
