package com.example.collectqr;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;
import java.util.HashMap;

public class QRCode {
    private final String sha256;
    private final Integer points;
    private final Double latitude;
    private final Double longitude;
    private final GeoLocation location;
    private final String qr_image;
    private final HashMap<String, String> scanned_by;
    private final HashMap<String, String> comments;
    private final ArrayList<String> all_images;

    public QRCode(String sha256, Double latitude, Double longitude) {
        this.sha256 = sha256;
        this.points = 0; //this needs to be updated immediately
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new GeoLocation(latitude, longitude);
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

    /**
     * Computes a GeoHash with a longitude and latitude.
     * <a href="https://firebase.google.com/docs/firestore/solutions/geoqueries#java">Source</a>
     *
     * @return A string-representation of a GeoHash
     */
    public String getGeoHash() {
        return GeoFireUtils.getGeoHashForLocation(location);
    }

    /**
     * Return a latitude, usually a Double, as a String.
     *
     * @return The latitude of a location as a String.
     */
    public String getLatitudeAsString() {
        return String.valueOf(latitude);
    }

    /**
     * Return a longitude, usually a Double, as a String.
     *
     * @return The longitude of a location as a String.
     */
    public String getLongitudeAsString() {
        return String.valueOf(longitude);
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
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

    public GeoLocation getLocation() {
        return location;
    }
}
