package com.example.collectqr.ui.history;

public class HistoryItem {
    private int points;
    private int imageResource;
    private String hash;

    public HistoryItem(int points, int imageResource, String hash) {
        this.points = points;
        this.imageResource = imageResource;
        this.hash = hash;
    }

    public int getPoints() {
        return points;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getHash() {
        return hash;
    }
}
