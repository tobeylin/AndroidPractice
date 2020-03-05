package sample.app.tobeylin.androidpractice.viewpager.tab;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.google.android.material.tabs.TabLayout;

public class VisibleTabListener implements TabLayout.OnTabSelectedListener {

    private FragmentStatePagerAdapter tabAdapter;

    public VisibleTabListener(FragmentStatePagerAdapter tabAdapter) {
        this.tabAdapter = tabAdapter;
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        final int tabPosition = tab.getPosition();
        final Fragment fragment = tabAdapter.getItem(tabPosition);

        tabAdapter.setPrimaryItem(((ViewGroup) tab.getCustomView().getParent()), tabPosition, fragment);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
