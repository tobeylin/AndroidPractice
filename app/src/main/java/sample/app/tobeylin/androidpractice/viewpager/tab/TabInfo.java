package sample.app.tobeylin.androidpractice.viewpager.tab;

import androidx.fragment.app.Fragment;

public class TabInfo {

    public String title;
    public Fragment fragment;

    public TabInfo(String title, Fragment fragment) {
        this.title = title;
        this.fragment = fragment;
    }

}