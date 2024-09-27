package com.Hadeer.myapplication;

import android.content.Intent;
import android.os.Bundle;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.Hadeer.myapplication.databinding.ActivityMainBinding;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;


    private TabAdapter tabAdapter = new TabAdapter(getSupportFragmentManager(), getLifecycle());
    private ArrayList<TabModel> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //add fragment in array
        list.add(new TabModel(new HomeFragment(), R.drawable.ic_home));
        list.add(new TabModel(new FavoriteFragment(), R.drawable.ic_favorite));


        // send to adapter list of fragment
        tabAdapter.setFragments(list);
        // adapter show fragment in view pager
        binding.pager.setAdapter(tabAdapter);

        new TabLayoutMediator(binding.tabLayout, binding.pager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {

                tab.setIcon(list.get(position).getImageResId());
            }
        }).attach();


        binding.search.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }


}