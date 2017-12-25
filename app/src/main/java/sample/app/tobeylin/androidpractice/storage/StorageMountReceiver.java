package sample.app.tobeylin.androidpractice.storage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.HashSet;
import java.util.Set;

public class StorageMountReceiver extends BroadcastReceiver {

    private static final String TAG = StorageMountReceiver.class.getSimpleName();

    private Set<String> mountPoints = new HashSet<>();

    public static void register(Context context, StorageMountReceiver storageMountReceiver) {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
        intentFilter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        intentFilter.addDataScheme("file");
        context.registerReceiver(storageMountReceiver, intentFilter);
    }

    public static void unregister(Context context, StorageMountReceiver storageMountReceiver) {
        context.unregisterReceiver(storageMountReceiver);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        String mountPoint = intent.getData().getPath();

        synchronized (this) {
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
                if (!mountPoints.contains(mountPoint)) {
                    mountPoints.add(mountPoint);
                    Log.i(TAG, "action = " + action + ", mount point = " + mountPoint);
                } else {
                    Log.i(TAG, "Ignore the duplicated mounted event.");
                }
            } else if (action.equals(Intent.ACTION_MEDIA_UNMOUNTED)) {
                if (mountPoint.contains(mountPoint)) {
                    mountPoints.remove(mountPoint);
                    Log.i(TAG, "action = " + action + ", mount point = " + mountPoint);
                } else {
                    Log.i(TAG, "Ignore the duplicated unmounted event.");
                }
            }
        }
    }

}
