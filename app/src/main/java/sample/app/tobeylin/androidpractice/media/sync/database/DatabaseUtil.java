package sample.app.tobeylin.androidpractice.media.sync.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.RawRes;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseUtil {

    private static final String TAG = DatabaseUtil.class.getSimpleName();

    public static void executeSQLFromFile(Context context, SQLiteDatabase db, @RawRes int sqlFileResource) {
        InputStream sqlInputStream = context.getResources().openRawResource(sqlFileResource);
        try {
            final char delimiter = ';';
            List<String> sqlList = read(sqlInputStream, delimiter);
            for (String sql : sqlList) {
                db.execSQL(sql);
                Log.i(TAG, "execute SQL from raw file. " + sql);
            }
        } catch (Exception e) {
            Log.w(TAG, "failed to execute SQL from raw file. " + e.getMessage());
        } finally {
            close(sqlInputStream);
        }
    }

    public static List<String> read(InputStream inStream, char delimiter) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        List<String> sqlList = new ArrayList<>();
        int byteData;
        while ((byteData = inStream.read()) != -1) {
            if (byteData != delimiter) {
                bos.write(byteData);
            } else {
                sqlList.add(bos.toString());
                bos.reset();
            }
        }
        close(bos);

        return sqlList;
    }

    public static void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
