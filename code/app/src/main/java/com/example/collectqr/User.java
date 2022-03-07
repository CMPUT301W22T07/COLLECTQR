package com.example.collectqr;

import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String username;
    private String email;
    private String phone;
    private HashMap<String, Integer> stats;
    private HashMap<String, HashMap<String, String>> codes_scanned; //sha : date scanned

    public User(String username) {
        this.username = username;
        this.email = "";
        this.phone = "";

        HashMap<String, Integer> stats = new HashMap<>();
        stats.put("num_codes", 0);
        stats.put("total_points", 0);
        this.stats = stats;

        this.codes_scanned = new HashMap<String, HashMap<String, String>>();
    }

    public void addCode(String sha, Integer points, String latitude, String longitude, String date) {
        HashMap<String, String> inner = new HashMap<>();
        inner.put("points", points.toString());
        inner.put("latitude", latitude);
        inner.put("longitude", longitude);
        inner.put("date", date);
        this.codes_scanned.put(sha, inner);
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

    public HashMap<String, HashMap<String, String>> getCodes_scanned() {
        return codes_scanned;
    }
}
