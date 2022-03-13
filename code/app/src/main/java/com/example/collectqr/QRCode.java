package com.example.collectqr;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class which represents a QR Code.
 * It also contains information related to who has scanned the QR code, who has
 * commented on the QR code, and paths to images of the QR code, for the purposes
 * of this app.
 */
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

    /**
     * Returns the number of points a QR code is worth, based on its sha256 hash
     *
     * @return the number of points the QR Code is worth
     */
    public Integer getPoints() {
        return points;
    }

    /**
     * Stores the username of a user who scanned the QR Code, along
     * with the date they scanned the QR Code
     *
     * @param  user the username of a user who scanned the QR Code
     * @param  date the date the user scanned the QR Code
     */
    public void addScannedBy(String user, String date) {
        this.scanned_by.put(user, date);
    }

    /**
     * Stores a comment, along with the username of the user who commented
     * into the QR Code class
     *
     * @param  user the username of a user who commented on the QR Code
     * @param  comment the comment the user left
     */
    public void addComment(String user, String comment) {
        this.comments.put(user, comment);
    }

    /**
     * Adds the directory of an image into the list of all images
     *
     * @param  image the directory of the image
     */
    public void addImage(String image) {
        this.all_images.add(image);
    }

    /**
     * Returns the sha256 hash of the QR Code
     *
     * @return the QR Codes sha256 hash
     */
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

    /**
     * Returns the latitude of the QR Code, as a double
     *
     * @return the latitude of the QR Code
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Returns the longitude of the QR Code, as a double
     *
     * @return the longitude of the QR Code
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Returns the path to the main image of the QR Code
     *
     * @return The path of the QR Code image
     */
    public String getQr_image() {
        return qr_image;
    }

    /**
     * Returns a hashmap where each key is a username, and the value
     * is the date the user scanned the QR Code
     *
     * @return A hashmap of the users who have scanned the QR code, along with the date they scanned
     */
    public HashMap<String, String> getScanned_by() {
        return scanned_by;
    }

    /**
     * Returns a hashmap where each key is a username, and the value
     * is the comment the user left on the QR Code
     *
     * @return A hashmap of the users who have scanned the QR code, along with their comment
     */
    public HashMap<String, String> getComments() {
        return comments;
    }

    /**
     * Returns a list containing all the image paths of the QR Code
     *
     * @return A list of all image paths of the QR Code
     */
    public ArrayList<String> getAll_images() {
        return all_images;
    }

    /**
     * Returns the geolocation of the QR Code
     *
     * @return the geolocation of the QR code
     */
    public GeoLocation getLocation() {
        return location;
    }
}
