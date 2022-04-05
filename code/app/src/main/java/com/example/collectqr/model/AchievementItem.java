package com.example.collectqr.model;


public class AchievementItem {
    private int image;
    private String achievement;

    public AchievementItem(int image, String achievement) {
        this.image = image;
        this.achievement = achievement;
    }

    public int getImage() {
        return image;
    }

    public String getAchievement() {
        return achievement;
    }
}
