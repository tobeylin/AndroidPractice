package sample.app.tobeylin.androidpractice.viewpager.tab;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

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
