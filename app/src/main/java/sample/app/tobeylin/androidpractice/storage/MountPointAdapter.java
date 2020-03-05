package sample.app.tobeylin.androidpractice.storage;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MountPointAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> mountPoints;

    public MountPointAdapter(List<String> mountPoints) {
        this.mountPoints = mountPoints;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return MountPointViewHolder.newInstance(parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MountPointViewHolder) holder).bindData(mountPoints.get(position));
    }

    @Override
    public int getItemCount() {
        return mountPoints.size();
    }

}
