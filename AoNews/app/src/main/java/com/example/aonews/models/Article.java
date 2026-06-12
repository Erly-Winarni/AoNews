package com.example.aonews.models;

import com.google.gson.annotations.SerializedName;

public class Article {

    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("url")
    private String url;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("news_site")
    private String newsSite;

    @SerializedName("summary")
    private String summary;

    @SerializedName("published_at")
    private String publishedAt;

    @SerializedName("updated_at")
    private String updatedAt;
    public Article() {}

    public Article(int id, String title, String url, String imageUrl,
                   String newsSite, String summary, String publishedAt, String updatedAt) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.imageUrl = imageUrl;
        this.newsSite = newsSite;
        this.summary = summary;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
    }
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getUrl() { return url; }
    public String getImageUrl() { return imageUrl; }
    public String getNewsSite() { return newsSite; }
    public String getSummary() { return summary; }
    public String getPublishedAt() { return publishedAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setUrl(String url) { this.url = url; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setNewsSite(String newsSite) { this.newsSite = newsSite; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setPublishedAt(String publishedAt) { this.publishedAt = publishedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
