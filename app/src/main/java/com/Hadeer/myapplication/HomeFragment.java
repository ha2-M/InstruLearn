package com.Hadeer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.Hadeer.myapplication.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private VideoAdapter recommendedVideoAdapter;
    private CategoryAdapter categoryAdapter;

//    private final String API_KEY ="AIzaSyA_dwy51PBoEYiM_1ecacfttwT2d8s9dpc";
    private final String API_KEY ="AIzaSyCultoi3c2MPTMFOo7cXRTVTHNMbMLskeo";
   private List<CategoryModel> categories = new ArrayList<>();
    private Handler sliderHandler = new Handler(); // For image slider
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);


        setupImageSlider();


        // Initialize adapters with click listeners
        List<YouTubeResponse.Item> favoriteVideos = loadFavoritesFromPreferences(); // Load favorites
        recommendedVideoAdapter = new VideoAdapter(this::onVideoClick,
                requireContext(),favoriteVideos);
        categoryAdapter = new CategoryAdapter(categories, this::onCategoryClick);




        // Add default categories
        categories.add(new CategoryModel("Guitar", R.drawable.guitar));
        categories.add(new CategoryModel("Piano", R.drawable.piano));
        categories.add(new CategoryModel("Clarinet", R.drawable.clarient));
        categories.add(new CategoryModel("Violin", R.drawable.violin));
        categories.add(new CategoryModel("Drums", R.drawable.drums));
        categories.add(new CategoryModel("Oud", R.drawable.oud));
        categories.add(new CategoryModel("Accordion", R.drawable.accordion));


        // Setup RecyclerViews with LayoutManagers
        binding.categoryRecyclerView.setAdapter(categoryAdapter);
        binding.PopularRecyclerView.setAdapter(recommendedVideoAdapter);

        // Load default category videos if the list is not empty
        if (!categories.isEmpty()) {
            loadDiscoverVideos(categories.get(0).getName());
        }

        return binding.getRoot();

    }

    private List<YouTubeResponse.Item> loadFavoritesFromPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("favorite_videos", null);
        Type type = new TypeToken<List<YouTubeResponse.Item>>() {}.getType();
        List<YouTubeResponse.Item> favorites = gson.fromJson(json, type);
        return favorites != null ? favorites : new ArrayList<>();
    }
    //fetch videos from youtube
    private void loadDiscoverVideos(String category) {
        YouTubeApiService apiService = RetrofitClient.getClient().create(YouTubeApiService.class);
        String query = "music " + category + " lesson for beginners";

        apiService.searchMusicVideos("snippet", query, "US", API_KEY, 16)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YouTubeResponse>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull YouTubeResponse response) {
                        if (response.getItems() != null && !response.getItems().isEmpty()) {
                            recommendedVideoAdapter.setVideoList(response.getItems());
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    // Handling video click
    private void onVideoClick(YouTubeResponse.Item videoItem) {
        String videoId = videoItem.getId().getVideoId();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }

    // Handle category click
    private void onCategoryClick(CategoryModel category) {
        Intent intent = new Intent(getActivity(), CategoryDetailActivity.class);
        intent.putExtra("category_name", category.getName());
        intent.putExtra("category_image", category.getImageUrl());
        startActivity(intent);
    }


    // Image slider setup
    private void setupImageSlider() {
        List<Integer> imageList = new ArrayList<>();
        imageList.add(R.drawable.image1);
        imageList.add(R.drawable.image2);
        imageList.add(R.drawable.image3);


        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter(imageList);
        binding.viewPager.setAdapter(sliderAdapter);

        // Auto-scroll the image slider
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                sliderHandler.removeCallbacks(sliderRunnable);
                sliderHandler.postDelayed(sliderRunnable, 5000);
            }
        });


    }

    // Runnable for auto-scrolling images
    private final Runnable sliderRunnable = new Runnable() {
        @Override
        public void run() {
            int currentItem = binding.viewPager.getCurrentItem();
            binding.viewPager.setCurrentItem((currentItem + 1) % 3);
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);

    }



    @Override
    public void onResume() {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        disposable.clear();
    }

}
