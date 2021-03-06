package com.example.collectqr.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * A class which represents a user of the app.
 * Also contains information related to the player stats, along with information about
 * what QR Codes they have scanned
 */
public class User {
    private final String username;
    private String email;
    private String phone;
    private ArrayList<String> devices;
    private HashMap<String, Integer> stats;
    private final HashMap<String, HashMap<String, Object>> codes_scanned; //sha : date scanned
    //achievements
    private boolean scan_1_code;
    private boolean scan_10_codes;
    private boolean scan_50_codes;
    private boolean scan_10_points;
    private boolean scan_100_points;
    private boolean scan_300_points;

    /**
     *
     * It is a constructor.
     *
     * @param username  the username
     */
    public User(String username) {

        this.username = username;
        this.email = "";
        this.phone = "";
        this.devices = new ArrayList<>();
        this.scan_1_code=false;
        this.scan_10_codes=false;
        this.scan_50_codes=false;
        this.scan_10_points=false;
        this.scan_100_points=false;
        this.scan_300_points=false;

        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("num_codes", 0);
        stats.put("total_points", 0);
        stats.put("best_code", 0);
        stats.put("region_best", 0);
        this.stats = stats;

        this.codes_scanned = new HashMap<>();
    }

    /**
     * Adds the data of a given QR Code to the users hashmap of scanned QR Codes
     *
     * @param  hash the sha256 hash of the QR Code
     * @param  points the number of points the QR code is worth
     * @param  latitude the latitude of the QR code
     * @param  longitude the longitude of the QR code
     * @param  geohash the geohash of the QR code
     * @param  date the date the QR code was scanned
     * @param  image the path to the image taken of the QR code
     */

    public void addCode(String hash, Integer points, String latitude, String longitude,
                        String geohash, Date date, String image) {

        HashMap<String, Object> inner = new HashMap<>();
        inner.put("points", points.toString());
        inner.put("latitude", latitude);
        inner.put("longitude", longitude);
        inner.put("geohash", geohash);
        inner.put("date", date);
        inner.put("image", image);
        this.codes_scanned.put(hash, inner);
    }

    /**
     * Returns the user's username
     *
     * @return The user's username
     */
    public String getUsername() {

        return username;
    }

    /**
     * Returns the user's email
     *
     * @return The user's email
     */
    public String getEmail() {

        return email;
    }

    /**
     * Sets the user's email to the given email
     *
     * @param email the user's new email
     */
    public void setEmail(String email) {

        this.email = email;
    }

    /**
     * Returns the user's phone number
     *
     * @return The user's phone number
     */
    public String getPhone() {

        return phone;
    }

    /**
     * Sets the user's phone number to the given phone number
     *
     * @param phone the user's new phone number
     */
    public void setPhone(String phone) {

        this.phone = phone;
    }

    /**
     * Returns the list of all the user's devices
     *
     * @return A list of all the user's devices
     */
    public ArrayList<String> getDevices() {

        return devices;
    }

    /**
     * Adds the given device to the user's list of devices
     *
     * @param device another device belonging to the user
     */
    public void addDevice(String device) {

        this.devices.add(device);
    }

    /**
     * Returns a hashmap containing the users stats. Stored in the format <String, Integer>,
     * where String is the type of stats, and Integer is the value.
     *
     * @return A hashmap containing information about the users stats
     */
    public HashMap<String, Integer> getStats() {

        return stats;
    }

    /**
     * Returns the HashMap containing information about the QR codes the user has scanned. The
     * innermost hashmap is what contains specific information about the QR codes, such as its
     * hash and the date it was scanned.
     *
     * @return A hashmap containing information about the QR codes the user has scanned
     */
    public HashMap<String, HashMap<String, Object>> getCodes_scanned() {

        return codes_scanned;
    }

    /**
     * Updates the users score hashmap with the given values
     *
     * @param  num_codes the number of codes the user has scanned
     * @param  total_points the total_points the user has
     */

    public void updateScore(int num_codes, int total_points, int best_code, int region_best) {
        stats.replace("num_codes", num_codes);
        stats.replace("total_points", total_points);
        stats.replace("best_code", best_code);
        stats.replace("region_best", region_best);
    }

    /**
     * Returns whether the user has the scan_1_code achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_1_code() {
        return scan_1_code;
    }

    /**
     * Sets whether the user has the scan_1_code achievement or not
     *
     */
    public void setScan_1_code(boolean scan_1_code) {
        this.scan_1_code = scan_1_code;
    }

    /**
     * Returns whether the user has the scan_10_codes achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_10_codes() {
        return scan_10_codes;
    }

    /**
     * Sets whether the user has the scan_10_codes achievement or not
     *
     */
    public void setScan_10_codes(boolean scan_10_codes) {
        this.scan_10_codes = scan_10_codes;
    }

    /**
     * Returns whether the user has the scan_50_codes achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_50_codes() {
        return scan_50_codes;
    }

    /**
     * Sets whether the user has the scan_50_codes achievement or not
     *
     */
    public void setScan_50_codes(boolean scan_50_codes) {
        this.scan_50_codes = scan_50_codes;
    }

    /**
     * Returns whether the user has the scan_10_points achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_10_points() {
        return scan_10_points;
    }

    /**
     * Sets whether the user has the scan_10_points achievement or not
     *
     */
    public void setScan_10_points(boolean scan_10_points) {
        this.scan_10_points = scan_10_points;
    }

    /**
     * Returns whether the user has the scan_100_points achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_100_points() {
        return scan_100_points;
    }

    /**
     * Sets whether the user has the scan_100_points achievement or not
     *
     */
    public void setScan_100_points(boolean scan_100_points) {
        this.scan_100_points = scan_100_points;
    }

    /**
     * Returns whether the user has the scan_300_points achievement or not
     *
     * @return  whether the user has the achievement or not
     */
    public boolean isScan_300_points() {
        return scan_300_points;
    }

    /**
     * Sets whether the user has the scan_300_points achievement or not
     *
     */
    public void setScan_300_points(boolean scan_300_points) {
        this.scan_300_points = scan_300_points;
    }
}
