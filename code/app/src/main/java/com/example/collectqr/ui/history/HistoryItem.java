package com.example.collectqr.ui.history;

import com.example.collectqr.R;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;

public class HistoryItem {
    private int points;
    private int imageResource = R.drawable.ic_baseline_map;
    private String hash;
    private Date date;

    public HistoryItem(int points, int imageResource, String hash) {
        this.points = points;
        this.imageResource = imageResource;
        this.hash = hash;
    }

    public HistoryItem(QueryDocumentSnapshot doc) {
        points = Integer.parseInt(doc.get("points").toString());
        hash = doc.getString("hash");
        date = doc.getDate("date");
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

    public Date getDate() {
        return date;
    }
}
