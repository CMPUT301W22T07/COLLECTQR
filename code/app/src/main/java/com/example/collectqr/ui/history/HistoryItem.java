package com.example.collectqr.ui.history;

/**
 * A class to bundle information needed for each item in history
 */
public class HistoryItem {
    private int points;
    private int imageResource;
    private String hash;

    public HistoryItem(int points, int imageResource, String hash) {
        this.points = points;
        this.imageResource = imageResource;
        this.hash = hash;
    }

    /**
     * Returns the number of points a given QR Code is worth
     *
     * @return the number of points the QR code is worth
     */
    public int getPoints() {
        return points;
    }

    /**
     * Returns the path to the image of the QR code
     *
     * @return the path to the image of the QR code
     */
    public int getImageResource() {
        return imageResource;
    }

    /**
     * Returns the hash of the QR code
     *
     * @return the hash of the QR code
     */
    public String getHash() {
        return hash;
    }
}
