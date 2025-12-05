package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P)
public class SharedPreferencesLoginTest {

    private static final String PREF_FILE_KEY = "com.example.TriviaBattler.PREFERENCE_FILE_KEY";
    private static final String PREF_USER_ID_KEY = "PREFERENCE_USER_ID";

    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();

        prefs = context.getSharedPreferences(
                PREF_FILE_KEY,
                Context.MODE_PRIVATE
        );
        prefs.edit().clear().apply();
    }

    @Test
    public void testUserIdPersistsToSharedPreferences() {
        int userId = 42;

        prefs.edit().putInt(PREF_USER_ID_KEY, userId).apply();

        int stored = prefs.getInt(PREF_USER_ID_KEY, -1);

        assertEquals(userId, stored);
    }

    @Test
    public void testLogoutClearsUserId() {
        int userId = 99;

        prefs.edit().putInt(PREF_USER_ID_KEY, userId).apply();

        prefs.edit().putInt(PREF_USER_ID_KEY, -1).apply();

        int stored = prefs.getInt(PREF_USER_ID_KEY, 123);

        assertEquals(-1, stored);
    }
}