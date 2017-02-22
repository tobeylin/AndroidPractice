package sample.app.tobeylin.androidpractice.viewpager.tab;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;

public class BaseTabAdapter extends FragmentStatePagerAdapter {

    private ArrayList<TabInfo> tabInfos = new ArrayList<>();

    public BaseTabAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addTabInfo(TabInfo tabInfo) {
        tabInfos.add(tabInfo);
    }

    @Override
    public Fragment getItem(int position) {
        return tabInfos.get(position).fragment;
    }

    @Override
    public int getCount() {
        return tabInfos.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabInfos.get(position).title;
    }

}