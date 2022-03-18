package com.example.collectqr;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.app.Application;
import android.content.Context;
import android.test.mock.MockContext;

import androidx.test.core.app.ApplicationProvider;

import com.example.collectqr.utilities.Preferences;
import com.mapbox.maps.plugin.annotation.AnnotationConfig;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

public class PreferencesTest {
    Context context = ApplicationProvider.getApplicationContext();

    @Before
    public void setup() {
        Preferences.deletePreferences(context);
    }

    @Test
    public void testPreferences() {
        Preferences.savePreferences(context, "teststring1000");
        String pref = Preferences.loadPreferences(context);
        assertEquals("teststring1000", pref);
    }

    @After
    public void finish() {
        Preferences.deletePreferences(context);
    }
}
