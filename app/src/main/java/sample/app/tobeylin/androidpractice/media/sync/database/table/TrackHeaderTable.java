package sample.app.tobeylin.androidpractice.media.sync.database.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.NotNull;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import sample.app.tobeylin.androidpractice.media.sync.database.TrackDatabase;

@Table(database = TrackDatabase.class, name = "track_header")
public class TrackHeaderTable extends BaseModel {

    @Column(name = "_id")
    @PrimaryKey
    public long id;

    @Column(name = "file_relative_path")
    @NotNull
    public String fileRelativePath;

    @Column(name = "header_id")
    public long headerId;

}
