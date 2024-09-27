package com.Hadeer.myapplication;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface YouTubeApiService {

        @GET("search")
        Observable<YouTubeResponse> searchMusicVideos(
                @Query("part") String part,
                @Query("q") String query,
                @Query("regionCode") String regionCode,
                @Query("key") String apiKey,
                @Query("maxResults") int maxResults
        );
}
