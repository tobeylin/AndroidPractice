package sample.app.tobeylin.androidpractice.storage;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import sample.app.tobeylin.androidpractice.R;

class MountPointViewHolder extends RecyclerView.ViewHolder {

    private TextView mountPointPath;

    static MountPointViewHolder newInstance(ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new MountPointViewHolder(inflater.inflate(R.layout.item_mount_point, parent, false));
    }

    private MountPointViewHolder(View itemView) {
        super(itemView);

        mountPointPath = (TextView) itemView.findViewById(R.id.mountPointPath);
    }

    void bindData(String mountPoint) {
        mountPointPath.setText(mountPoint);
    }

}
