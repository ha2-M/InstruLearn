package com.Hadeer.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.Hadeer.myapplication.databinding.FragmentFavoriteBinding;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class FavoriteFragment extends Fragment {
    private FavoriteAdapter favoriteAdapter;
    private FragmentFavoriteBinding binding;
    private List<YouTubeResponse.Item> favoriteVideos;
    private static final String FAVORITES_KEY = "favorite_videos";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoriteVideos = loadFavoritesFromPreferences();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoriteBinding.inflate(inflater, container, false);
        favoriteAdapter = new FavoriteAdapter(this::onVideoClick, this);
        favoriteAdapter.setVideoList(favoriteVideos);
        binding.videosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.videosRecyclerView.setAdapter(favoriteAdapter);
        return binding.getRoot();
    }

    private void onVideoClick(YouTubeResponse.Item videoItem) {
        String videoId = videoItem.getId().getVideoId();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + videoId));
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Reload the favorites list
        favoriteVideos = loadFavoritesFromPreferences();
        favoriteAdapter.setVideoList(favoriteVideos);
    }

    public void removeFromFavorites(YouTubeResponse.Item video) {
        if (favoriteVideos.remove(video)) {
            saveFavoritesToPreferences(favoriteVideos);
            favoriteAdapter.notifyDataSetChanged(); // Consider using notifyItemRemoved in adapter
        }
    }

    private void saveFavoritesToPreferences(List<YouTubeResponse.Item> favoritesList) {
        SharedPreferences prefs = requireContext().getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoritesList);
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }

    private List<YouTubeResponse.Item> loadFavoritesFromPreferences() {
        SharedPreferences prefs = requireContext().getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString(FAVORITES_KEY, null);
        Type type = new TypeToken<List<YouTubeResponse.Item>>() {}.getType();
        List<YouTubeResponse.Item> favorites = gson.fromJson(json, type);
        return favorites != null ? favorites : new ArrayList<>();
    }


    public void onFavoritesChanged() {
        // إعادة تحميل الفيديوهات المفضلة من SharedPreferences
        List<YouTubeResponse.Item> favoriteVideos = loadFavoritesFromPreferences();
        favoriteAdapter.setVideoList(favoriteVideos); // تحديث قائمة المفضلة في المحول
    }
}
