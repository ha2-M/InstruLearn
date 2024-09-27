package com.Hadeer.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.Hadeer.myapplication.databinding.ItemFavoriteBinding;
import com.bumptech.glide.Glide;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.VideoViewHolder> {
    private List<YouTubeResponse.Item> videoList;
    private final OnVideoClickListener onVideoClickListener;
    private final FavoriteFragment favoriteFragment;

    public FavoriteAdapter(OnVideoClickListener onVideoClickListener, FavoriteFragment favoriteFragment) {
        this.onVideoClickListener = onVideoClickListener;
        this.favoriteFragment = favoriteFragment;
    }

    public void setVideoList(List<YouTubeResponse.Item> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFavoriteBinding binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VideoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        if (videoList != null && position >= 0 && position < videoList.size()) {
            YouTubeResponse.Item video = videoList.get(position);
            holder.bind(video);
        }
    }

    @Override
    public int getItemCount() {
        return videoList != null ? videoList.size() : 0;
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        private final ItemFavoriteBinding binding;

        public VideoViewHolder(ItemFavoriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(YouTubeResponse.Item video) {
            binding.videoTitle.setText(video.getSnippet().getTitle());
            binding.videoDescription.setText(video.getSnippet().getDescription());
            String thumbnailUrl = video.getSnippet().getThumbnails().getDefaultThumbnail().getUrl();
            Glide.with(binding.favImage.getContext()).load(thumbnailUrl).into(binding.favImage);

            // Handle video click
            binding.getRoot().setOnClickListener(v -> onVideoClickListener.onVideoClick(video));

            // Delete video from favorites
            binding.deleteButton.setOnClickListener(v -> {
                favoriteFragment.removeFromFavorites(video);
                notifyItemRemoved(getAdapterPosition());
            });
        }
    }

    public interface OnVideoClickListener {
        void onVideoClick(YouTubeResponse.Item videoItem);
    }
    public void updateFavorites(List<YouTubeResponse.Item> favorites) {
        this.videoList.clear();
        this.videoList.addAll(favorites);
        notifyDataSetChanged();
    }

}
