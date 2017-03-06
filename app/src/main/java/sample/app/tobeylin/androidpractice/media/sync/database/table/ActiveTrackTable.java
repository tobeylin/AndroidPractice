package sample.app.tobeylin.androidpractice.media.sync.database.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import sample.app.tobeylin.androidpractice.media.sync.database.TrackDatabase;

@Table(database = TrackDatabase.class, name = "active_track")
public class ActiveTrackTable extends BaseModel {

    @Column(name = "_id")
    @PrimaryKey(autoincrement = true)
    public long id;

    @Column(name = "file_absolute_path")
    @Unique
    public String fileAbsolutePath;

    @Column(name = "type")
    @NotNull
    public int type;

}
