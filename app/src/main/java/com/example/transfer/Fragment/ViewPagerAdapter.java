package com.example.transfer.Fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0)
        {
            return new recentFragment();
        }

        else if (position == 1)
        {
            return new aroundFragment();
        }
        else
        {
            return new infoFragment();
        }

    }

    @Override
    public int getCount() {
        return 3;
    }
}
