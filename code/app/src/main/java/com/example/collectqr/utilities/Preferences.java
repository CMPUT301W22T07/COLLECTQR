package com.example.collectqr.utilities;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * A class which handles saving and loading from shared preferences,
 * meant to make both processes easier
 */
public class Preferences {
    /**
     * Saves a given username to the users shared preferences
     *
     * @param  context
     * @param  username the username to be saved
     */
    public static void savePreferences(Context context, String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(username);
        editor.putString("username", json);
        editor.apply();
    }

    /**
     * Loads a username stores in the users shared preferences
     *
     * @param  context
     * @return the username stored in shared preferences
     */
    public static String loadPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("username", null);
        Type type = new TypeToken<String>() {}.getType();
        String username = gson.fromJson(json, type);
        return username;
    }

    /**
     * Deletes all of the users shared preferences
     *
     * @param  context
     */
    public static void deletePreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
