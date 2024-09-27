package com.Hadeer.myapplication;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class TabAdapter extends FragmentStateAdapter {

    private ArrayList<TabModel> fragments;

    public TabAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        fragments = new ArrayList<>(); // Initialize the list here
    }

    public void setFragments(ArrayList<TabModel> fragmentList) {
        this.fragments.clear();
        this.fragments.addAll(fragmentList);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragments.get(position).getFragment();
    }

    @Override
    public int getItemCount() {
            return fragments== null ?0 : fragments.size();
    }
    public int getFragmentPosition(Fragment fragment) {
        for (int i = 0; i < fragments.size(); i++) {
            if (fragments.get(i).getFragment().getClass().equals(fragment.getClass())) {
                return i;
            }
        }
        return -1; // Fragment not found
    }

    public void replaceFragment(Fragment newFragment, int position) {
        if (position >= 0 && position < fragments.size()) {
            fragments.set(position, new TabModel(newFragment, fragments.get(position).getImageResId()));
        }
    }
}
