package com.example.hkharton.testcanvas.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.hkharton.testcanvas.activities.LibraryActivity;
import com.example.hkharton.testcanvas.fragment.FlooringFragment;
import com.example.hkharton.testcanvas.fragment.HardwareFragment;
import com.example.hkharton.testcanvas.fragment.PaintFragment;
import com.example.hkharton.testcanvas.fragment.WallpaperFragment;
import com.example.hkharton.testcanvas.fragment.WoodFragment;

public class TabsPagerAdapter extends FragmentPagerAdapter{
    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index){
        switch (index) {
            case 0:
                return new WoodFragment();
            case 1:
                return new WallpaperFragment();
            case 2:
                return new PaintFragment();
            case 3:
                return new HardwareFragment();
            case 4:
                return new FlooringFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get the item count
        return LibraryActivity.materialTabs.length;
    }
}
