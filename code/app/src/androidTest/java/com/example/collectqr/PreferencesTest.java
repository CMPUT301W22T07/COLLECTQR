package com.example.collectqr;

import static org.junit.Assert.assertEquals;
import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.example.collectqr.utilities.Preferences;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * The class Preferences test
 */
public class PreferencesTest {
    Context context = ApplicationProvider.getApplicationContext();

    @Before

/**
 *
 * Setup
 *
 */
    public void setup() {

        Preferences.deletePreferences(context);
    }

    @Test

/**
 *
 * Test user name preferences
 *
 */
    public void testUserNamePreferences() {

        Preferences.saveUserName(context, "teststring1000");
        String pref = Preferences.loadUserName(context);
        assertEquals("teststring1000", pref);
    }

    @Test

/**
 *
 * Test admin preferences
 *
 */
    public void testAdminPreferences() {

        Preferences.saveAdminStatus(context, true);
        Boolean admin = Preferences.loadAdminStatus(context);
        assertEquals(true, admin);

        Preferences.saveAdminStatus(context, false);
        Boolean admin1 = Preferences.loadAdminStatus(context);
        assertEquals(false, admin1);
    }

    @After

/**
 *
 * Finish
 *
 */
    public void finish() {

        Preferences.deletePreferences(context);
    }
}
