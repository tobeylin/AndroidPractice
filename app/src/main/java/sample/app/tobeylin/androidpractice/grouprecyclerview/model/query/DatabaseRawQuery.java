package sample.app.tobeylin.androidpractice.grouprecyclerview.model.query;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DatabaseRawQuery implements DataQuery<String, Cursor> {

    private SQLiteDatabase database;
    private String query;
    private String[] selectionArgs;

    public DatabaseRawQuery(SQLiteDatabase database) {
        this(database, null, null);
    }

    public DatabaseRawQuery(SQLiteDatabase database, String query) {
        this(database, query, null);
    }

    public DatabaseRawQuery(SQLiteDatabase database, String query, String[] selectionArgs) {
        this.database = database;
        this.query = query;
        this.selectionArgs = selectionArgs;
    }

    public void setDatabase(SQLiteDatabase database) {
        this.database = database;
    }

    public void setQuery(String query, String[] selectionArgs) {
        this.query = query;
        this.selectionArgs = selectionArgs;
    }

    @Override
    public String getQuery() {
        return query;
    }

    @Override
    public Cursor getQueryResult() {
        if (query == null) {
            throw new IllegalArgumentException("Raw query is null/empty.");
        }

        if (!database.isOpen()) {
            throw new IllegalStateException("Database is closed.");
        }

        try {
            return database.rawQuery(query, selectionArgs);
        } catch (Exception e) {
            return null;
        }
    }

}
