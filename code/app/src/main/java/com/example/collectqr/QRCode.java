package com.example.collectqr;

import java.util.ArrayList;
import java.util.HashMap;

public class QRCode {
    private String sha256;
    private Integer points;
    private String latitude;
    private String longitude;
    private String qr_image;
    private HashMap<String, String> scanned_by;
    private HashMap<String, String> comments;
    private ArrayList<String> all_images;

    public QRCode(String sha256, String latitude, String longitude) {
        this.sha256 = sha256;
        this.points = 0; //this needs to be updated immediately
        this.latitude = latitude;
        this.longitude = longitude;
        this.qr_image = "";
        this.scanned_by = new HashMap<>();
        this.comments = new HashMap<>();
        this.all_images = new ArrayList<>();
    }

    public Integer getPoints() {
        return points;
    }

    public void addScannedBy(String user, String date) {
        this.scanned_by.put(user, date);
    }

    public void addComment(String user, String comment) {
        this.comments.put(user, comment);
    }

    public void addImage(String image) {
        this.all_images.add(image);
    }

    public String getSha256() {
        return sha256;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getQr_image() {
        return qr_image;
    }

    public HashMap<String, String> getScanned_by() {
        return scanned_by;
    }

    public HashMap<String, String> getComments() {
        return comments;
    }

    public ArrayList<String> getAll_images() {
        return all_images;
    }
}
