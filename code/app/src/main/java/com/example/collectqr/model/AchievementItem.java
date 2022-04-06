package com.example.collectqr.model;

/**
 * A class which stores the image and corresponding message
 * of an item in the list of achievements in the user profile
 */
public class AchievementItem {
    private int image;
    private String achievement;

    public AchievementItem(int image, String achievement) {
        this.image = image;
        this.achievement = achievement;
    }

    /**
     * Returns the image of the achievement item
     * @return the image of the achievement item
     */
    public int getImage() {
        return image;
    }

    /**
     * Returns the message of the achievement item
     * @return the message of the achievement item
     */
    public String getAchievement() {
        return achievement;
    }
}
