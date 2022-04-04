package com.example.collectqr.model;

import com.firebase.geofire.GeoFireUtils;
import com.firebase.geofire.GeoLocation;

import java.util.Date;
import java.util.HashMap;

/**
 * A class which represents a QR Code.
 * It also contains information related to who has scanned the QR code, who has
 * commented on the QR code, and paths to images of the QR code, for the purposes
 * of this app.
 */
public class QRCode {
    private final String sha256;
    private Integer points;
    private Double latitude;
    private Double longitude;
    private GeoLocation location;
    private String qr_image;
    private Date date;
    private final HashMap<String, String> scanned_by;
    private final HashMap<String, String> comments;


    /**
     *
     * It is a constructor.
     *
     * @param sha256  the sha256
     */
    public QRCode(String sha256) {
        this.sha256 = sha256;
        this.points = 0; //this needs to be updated immediately
        this.latitude = null;
        this.longitude = null;
        this.location = null;
        this.qr_image = "";
        this.scanned_by = new HashMap<>();
        this.comments = new HashMap<>();
        this.date = null;
    }

    /**
     *
     * It is a constructor.
     *
     * @param sha256  the sha256
     * @param latitude  the latitude
     * @param longitude  the longitude
     */
    public QRCode(String sha256, Double latitude, Double longitude) {
        this.sha256 = sha256;
        this.points = 0; //this needs to be updated immediately
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = new GeoLocation(latitude, longitude);
        this.qr_image = "";
        this.scanned_by = new HashMap<>();
        this.comments = new HashMap<>();
        this.date = null;
    }


    /**
     *
     * It is a constructor.
     *
     * @param sha256  the sha256
     * @param points  the points
     * @param date  the date
     * @param qr_image  the qr_image
     */
    public QRCode(String sha256, Integer points, Date date, String qr_image) {
        this.sha256 = sha256;
        this.points = points;
        this.date = date;
        this.qr_image = qr_image;
        this.latitude = null;
        this.longitude = null;
        this.location = null;
        this.scanned_by = null;
        this.comments = null;
    }

    /**
     * Returns the date when the user scanned the QR code
     *
     * @return the date when the user scanned the QR code
     */
    public Date getDate() { return date; }

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
     * Returns the geolocation of the QR Code
     *
     * @return the geolocation of the QR code
     */
    public GeoLocation getLocation() {
        return location;
    }

    /**
     * Sets the points of the QR Code
     * @param points
     */
    public void setPoints(Integer points) { this.points = points; }

    /**
     * Returns the date when the QR code was scanned
     * @param date the date when the QR code was scanned
     */
    public void setDate(Date date) { this.date = date; }

    /**
     * Sets the name of the image of the code
     * @param image
     */
    public void setQr_image(String image) { this.qr_image=image;}

    /**
     * Sets the latitude, longitude, and location of the QR Code
     * using a given latitude and longitude
     *
     * @param latitude
     * @param longitude
     */
    public void setAllLocations(Double latitude, Double longitude) {
        if(latitude == null || longitude == null) {
            this.latitude = null;
            this.longitude = null;
            this.location = null;
        } else {
            this.latitude = latitude;
            this.longitude = longitude;
            this.location = new GeoLocation(latitude, longitude);
        }
    }
}
