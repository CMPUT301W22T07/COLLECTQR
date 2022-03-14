package com.example.collectqr;

import java.util.Date;
import java.util.HashMap;

/**
 * A class which represents a user of the app.
 * Also contains information related to the player stats, along with information about
 * what QR Codes they have scanned
 */
public class User {
    private final String username;
    private final String email;
    private final String phone;
    private final HashMap<String, Integer> stats;
    private final HashMap<String, HashMap<String, Object>> codes_scanned; //sha : date scanned

    public User(String username) {
        this.username = username;
        this.email = "";
        this.phone = "";

        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("num_codes", 0);
        stats.put("total_points", 0);
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
     * Returns the user's phone number
     *
     * @return The user's phone number
     */
    public String getPhone() {
        return phone;
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
}
