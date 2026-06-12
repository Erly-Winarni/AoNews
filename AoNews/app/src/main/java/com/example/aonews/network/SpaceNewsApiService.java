package com.example.aonews.network;

import com.example.aonews.models.ArticleResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SpaceNewsApiService {
    @GET("articles/")
    Call<ArticleResponse> getArticles(
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("ordering") String ordering
    );
    @GET("articles/")
    Call<ArticleResponse> searchArticles(
            @Query("search") String query,
            @Query("limit") int limit,
            @Query("offset") int offset
    );
    @GET("blogs/")
    Call<ArticleResponse> getBlogs(
            @Query("limit") int limit,
            @Query("offset") int offset
    );
    @GET("reports/")
    Call<ArticleResponse> getReports(
            @Query("limit") int limit,
            @Query("offset") int offset
    );
}
