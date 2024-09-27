package com.Hadeer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Hadeer.myapplication.databinding.ActivityCategoryDetailBinding;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.Hadeer.myapplication.databinding.ActivityCategoryDetailBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryDetailActivity extends AppCompatActivity implements VideoAdapter.OnVideoClickListener {

    private ActivityCategoryDetailBinding binding;
    private VideoAdapter videoAdapter;
    private String categoryName;
    private CompositeDisposable disposable = new CompositeDisposable();
//    private final String API_KEY ="AIzaSyA_dwy51PBoEYiM_1ecacfttwT2d8s9dpc";
    private final String API_KEY ="AIzaSyCultoi3c2MPTMFOo7cXRTVTHNMbMLskeo";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCategoryDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get category name,image from intent
        Intent intent = getIntent();
        categoryName = intent.getStringExtra("category_name");
        int categoryImageResId = intent.getIntExtra("category_image",0);


        // Set category name and image
        binding.categoryNameTextView.setText(categoryName);
        binding.categoryImageView.setImageResource(categoryImageResId);

        // Initialize VideoAdapter with this activity as the listener
        List<YouTubeResponse.Item> favoriteVideos = loadFavoritesFromPreferences();
        videoAdapter = new VideoAdapter(this::onVideoClick, this,favoriteVideos);


        // Setup RecyclerView for videos
        binding.videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.videosRecyclerView.setAdapter(videoAdapter);

        // Load videos related to the category
        loadVideosForCategory(categoryName);

    }
    private List<YouTubeResponse.Item> loadFavoritesFromPreferences() {
        SharedPreferences prefs = this.getSharedPreferences("favorites_prefs",
                Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("favorite_videos", null);
        Type type = new TypeToken<List<YouTubeResponse.Item>>() {}.getType();
        List<YouTubeResponse.Item> favorites = gson.fromJson(json, type);
        return favorites != null ? favorites : new ArrayList<>();
    }
    private void loadVideosForCategory(String category) {
        YouTubeApiService apiService = RetrofitClient.getClient().create(YouTubeApiService.class);
        String query = "lesson " + category+"for beginner";

        apiService.searchMusicVideos("snippet", query, "US", API_KEY, 20)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YouTubeResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull YouTubeResponse response) {
                        if (response != null && response.getItems() != null) {
                            List<YouTubeResponse.Item> videos = response.getItems();
                            videoAdapter.setVideoList(videos);
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onVideoClick(YouTubeResponse.Item videoItem) {
        String videoUrl = "https://www.youtube.com/watch?v=" + videoItem.getId().getVideoId();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(videoUrl));
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        disposable.clear();
    }
}
