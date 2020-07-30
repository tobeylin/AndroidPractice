package sample.app.tobeylin.androidpractice.viewpager;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import sample.app.tobeylin.androidpractice.R;

public class ViewPagerFragment extends Fragment implements View.OnClickListener {

    public static final String TAG = ViewPagerFragment.class.getSimpleName();

    public static ViewPagerFragment newInstance() {
        return new ViewPagerFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);

        Button button = (Button) view.findViewById(R.id.replaceViewPagerButton);
        button.setOnClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, this + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, this + " onPause");
    }

    @Override
    public void onClick(View v) {
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_view_pager, ViewPagerFragment.newInstance(), ViewPagerFragment.TAG)
                .addToBackStack(ViewPagerFragment.TAG)
                .commitAllowingStateLoss();
    }
}
