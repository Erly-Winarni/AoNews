package com.example.aonews.models;

public class SpaceFact {
    private String text;
    private boolean isFavorite;

    public SpaceFact(String text) {
        this.text = text;
        this.isFavorite = false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
