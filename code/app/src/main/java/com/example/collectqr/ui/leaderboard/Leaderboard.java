package com.example.collectqr.ui.leaderboard;


import com.example.collectqr.User;

import java.util.ArrayList;

/**
 * Model class for Leaderboard
 * containing an instance of the list of user objects,
 * and the signed in user's name and score
 */
public class Leaderboard {
    public ArrayList<User> leaderboardList;
    private String personalUsername;
    private Integer personalScore;

    /**
     * Initializes Leaderboard object
     * @param personalUsername
     * @param personalScore
     * @param userList
     */
    public Leaderboard(String personalUsername, Integer personalScore, ArrayList<User> userList){
        this.personalUsername = personalUsername;
        this.personalScore = personalScore;
        this.leaderboardList = userList;
    }

    /**
     * returns signed in user's username
     * @return personalUsername
     */
    public String getPersonalUsername(){
        return personalUsername;
    }

    /**
     * returns signed in user's score
     * @return personalUsername
     */
    public Integer getPersonalScore(){
        return personalScore;
    }
}
