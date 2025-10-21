package com.example.lr1_1;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position==0)
            return "КАРТА";
        if (position==1)
            return "СПИСОК ЗАДАЧ";
        return "ЗАЯВКА";
    }

    @NonNull
    @Override
    public Fragment getItem(int i) {
        Log.w("TypePager", String.valueOf(i));
        if (i == 0)
            return DetailsFragment.newInstance("map");
        if (i == 1)
            return DetailsFragment.newInstance("tasks");
        return DetailsFragment.newInstance("proposal");
    }

    @Override
    public int getCount() {
        return 3;
    }

}

