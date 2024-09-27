package com.Hadeer.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Hadeer.myapplication.databinding.ItemVideoBinding;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    private List<YouTubeResponse.Item> videoList;
    private final OnVideoClickListener onVideoClickListener;
    private List<YouTubeResponse.Item> favoriteVideos;
    private final Context context;
    private static final String FAVORITES_KEY = "favorite_videos";

    public VideoAdapter(OnVideoClickListener onVideoClickListener, Context context, List<YouTubeResponse.Item> favorites) {
        this.onVideoClickListener = onVideoClickListener;
        this.context = context;
        this.favoriteVideos = favorites;
    }

    public void setVideoList(List<YouTubeResponse.Item> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVideoBinding binding = ItemVideoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VideoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        if (videoList != null && position >= 0 && position < videoList.size()) {
            YouTubeResponse.Item video = videoList.get(position);
            holder.bind(video, favoriteVideos);
        }
    }

    @Override
    public int getItemCount() {
        return videoList != null ? videoList.size() : 0;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ItemVideoBinding binding;

        public VideoViewHolder(ItemVideoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(YouTubeResponse.Item video, List<YouTubeResponse.Item> favoriteVideos) {
            binding.videoTitle.setText(video.getSnippet().getTitle());
            binding.videoDescription.setText(video.getSnippet().getDescription());
            String thumbnailUrl = video.getSnippet().getThumbnails().getDefaultThumbnail().getUrl();
            Glide.with(binding.videoImage.getContext()).load(thumbnailUrl).into(binding.videoImage);

            binding.getRoot().setOnClickListener(v -> {
                if (onVideoClickListener != null) {
                    onVideoClickListener.onVideoClick(video);
                }
            });

            // Check if video is in favorites
            if (favoriteVideos.contains(video)) {
                binding.favButton.setColorFilter(Color.parseColor("#FFD700"));
            } else {
                binding.favButton.setColorFilter(Color.WHITE);
            }

            // Set OnClickListener for the favorite button
            binding.favButton.setOnClickListener(v -> {
                if (favoriteVideos.contains(video)) {
                    favoriteVideos.remove(video);
                    binding.favButton.setColorFilter(Color.WHITE);
                    Toast.makeText(v.getContext(), "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    favoriteVideos.add(video);
                    binding.favButton.setColorFilter(Color.parseColor("#FFD700"));
                    Toast.makeText(v.getContext(), "Added to favorites", Toast.LENGTH_SHORT).show();
                }

                saveFavoritesToPreferences();
                notifyItemChanged(getAdapterPosition());
            });
        }
    }

    private void saveFavoritesToPreferences() {
        SharedPreferences prefs = context.getSharedPreferences("favorites_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(favoriteVideos);
        editor.putString(FAVORITES_KEY, json);
        editor.apply();
    }

    public interface OnVideoClickListener {
        void onVideoClick(YouTubeResponse.Item videoItem);
    }
}