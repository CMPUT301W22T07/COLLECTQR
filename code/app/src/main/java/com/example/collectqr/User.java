package com.example.collectqr;

import java.util.Date;
import java.util.HashMap;

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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public HashMap<String, Integer> getStats() {
        return stats;
    }

    public HashMap<String, HashMap<String, Object>> getCodes_scanned() {
        return codes_scanned;
    }
}
