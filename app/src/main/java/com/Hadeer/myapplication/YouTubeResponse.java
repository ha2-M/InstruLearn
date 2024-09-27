package com.Hadeer.myapplication;

import java.util.List;

import com.google.gson.annotations.SerializedName;
import java.util.List;
public class YouTubeResponse {

    @SerializedName("items")
    private List<Item> items;

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        @SerializedName("id")
        private Id id;

        @SerializedName("snippet")
        private Snippet snippet;

        public Id getId() {
            return id;
        }

        public void setId(Id id) {
            this.id = id;
        }

        public Snippet getSnippet() {
            return snippet;
        }

        public void setSnippet(Snippet snippet) {
            this.snippet = snippet;
        }


        public static class Id {
            @SerializedName("videoId")
            private String videoId;

            // Constructor
            public Id(String videoId) {
                this.videoId = videoId;
            }

            public String getVideoId() {
                return videoId;
            }

            public void setVideoId(String videoId) {
                this.videoId = videoId;
            }
        }
    }


    public static class Snippet {
        @SerializedName("title")
        private String title;

        @SerializedName("description")
        private String description;

        @SerializedName("thumbnails")
        private Thumbnails thumbnails;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public Thumbnails getThumbnails() {
            return thumbnails;
        }

        public void setThumbnails(Thumbnails thumbnails) {
            this.thumbnails = thumbnails;
        }
    }

    public static class Thumbnails {
        @SerializedName("default")
        private ThumbnailDetails defaultThumbnail;

        @SerializedName("medium")
        private ThumbnailDetails medium;

        @SerializedName("high")
        private ThumbnailDetails high;

        public ThumbnailDetails getDefaultThumbnail() {
            return defaultThumbnail;
        }

        public void setDefaultThumbnail(ThumbnailDetails defaultThumbnail) {
            this.defaultThumbnail = defaultThumbnail;
        }

        public ThumbnailDetails getMedium() {
            return medium;
        }

        public void setMedium(ThumbnailDetails medium) {
            this.medium = medium;
        }

        public ThumbnailDetails getHigh() {
            return high;
        }

        public void setHigh(ThumbnailDetails high) {
            this.high = high;
        }
    }

    public static class ThumbnailDetails {
        @SerializedName("url")
        private String url;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}
