package com.Hadeer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.Hadeer.myapplication.databinding.ActivitySearchBinding;
import com.Hadeer.myapplication.databinding.FragmentHomeBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchActivity extends AppCompatActivity {
    private ActivitySearchBinding binding;
    private VideoAdapter searchAdapter;
    private List<YouTubeResponse.Item> videoList = new ArrayList<>();
    private YouTubeApiService apiService;
    //    private final String API_KEY = "AIzaSyA3gumDts3DZS4_uj_xxFmqbuepXP1yPus";
    //    private final String API_KEY ="AIzaSyA_dwy51PBoEYiM_1ecacfttwT2d8s9dpc";
    private final String API_KEY ="AIzaSyCultoi3c2MPTMFOo7cXRTVTHNMbMLskeo";
    private CompositeDisposable disposable = new CompositeDisposable();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        apiService = RetrofitClient.getClient().create(YouTubeApiService.class);

        // Initialize VideoAdapter with an empty list
        List<YouTubeResponse.Item> favoriteVideos = loadFavoritesFromPreferences();
        searchAdapter = new VideoAdapter(this::onVideoClick, this,favoriteVideos);

        searchAdapter.setVideoList(videoList);
        binding.videosRecyclerView.setAdapter(searchAdapter);
        binding.videosRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Handle search query input
        binding.search.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchVideos(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    // Show placeholder image when search is cleared
                    binding.placeholderImage.setVisibility(View.VISIBLE);
                    binding.placeholderText.setVisibility(View.VISIBLE);
                    binding.videosRecyclerView.setVisibility(View.GONE);
                    videoList.clear();
                    searchAdapter.notifyDataSetChanged();
                } else {
                    // Perform search
                    searchVideos(newText);
                }
                return true;
            }
        });

        // Initially show the placeholder image
        binding.placeholderImage.setVisibility(View.VISIBLE);
        binding.placeholderText.setVisibility(View.VISIBLE);
        binding.videosRecyclerView.setVisibility(View.GONE);
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
    private void searchVideos(String query) {
       //list for musical instruments
        List<String> musicalInstruments = Arrays.asList("piano", "guitar", "drums", "violin",
                "flute", "Clarinet", "saxophone", "trumpet", "accordion","oud","Accordion");

        boolean isInstrumentRelated = false;
        for (String instrument : musicalInstruments) {
            if (query.toLowerCase().contains(instrument.toLowerCase())) {
                isInstrumentRelated = true;
                break;
            }
        }


        String modifiedQuery = query + " lessons";
        apiService.searchMusicVideos("snippet", modifiedQuery, "US",
                        API_KEY, 10)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YouTubeResponse>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull YouTubeResponse response) {
                        List<YouTubeResponse.Item> results = response.getItems();

                        if (results == null || results.isEmpty()) {
                            binding.placeholderImage.setVisibility(View.VISIBLE);
                            binding.placeholderText.setVisibility(View.VISIBLE);
                            binding.videosRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.placeholderImage.setVisibility(View.GONE);
                            binding.placeholderText.setVisibility(View.GONE);
                            binding.videosRecyclerView.setVisibility(View.VISIBLE);
                            videoList.clear();
                            videoList.addAll(results);
                            searchAdapter.notifyDataSetChanged();
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


    private void onVideoClick(YouTubeResponse.Item videoItem) {
        String videoId = videoItem.getId().getVideoId();
        Intent intent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
        disposable.clear();
    }

}