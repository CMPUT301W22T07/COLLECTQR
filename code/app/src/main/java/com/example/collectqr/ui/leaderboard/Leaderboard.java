package com.example.collectqr.ui.leaderboard;


import java.util.ArrayList;

public class Leaderboard {
    public ArrayList<User> leaderboardList;
    private String personalUsername;
    private Integer personalScore;

    public Leaderboard(String personalUsername, Integer personalScore, ArrayList<User> userList){
        this.personalUsername = personalUsername;
        this.personalScore = personalScore;
        this.leaderboardList = userList;
    }

    public String getPersonalUsername(){
        return personalUsername;
    }

    public Integer getPersonalScore(){
        return personalScore;
    }
}
