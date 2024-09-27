package com.Hadeer.myapplication;

import androidx.fragment.app.Fragment;

public class TabModel {

    private Fragment fragment;

    private int imageResId;

    public TabModel(Fragment fragment, int imageResId) {
        this.fragment = fragment;
        this.imageResId = imageResId;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public int getImageResId() {
        return imageResId;
    }
}
