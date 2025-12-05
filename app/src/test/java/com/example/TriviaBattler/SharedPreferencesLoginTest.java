package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.database.entities.User;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class SharedPreferencesLoginTest {

    private Context context;
    private SharedPreferences prefs;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        prefs = context.getSharedPreferences(
                context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE
        );
        prefs.edit().clear().apply();
    }

    @Test
    public void testUserIdPersistsToSharedPreferences() {
        int userId = 42;


        prefs.edit().putInt(context.getString(R.string.preference_userId_key), userId).apply();


        int stored = prefs.getInt(context.getString(R.string.preference_userId_key), -1);

        assertEquals(userId, stored);
    }

    @Test
    public void testLogoutClearsUserId() {
        int userId = 99;
        prefs.edit().putInt(context.getString(R.string.preference_userId_key), userId).apply();

        prefs.edit().putInt(context.getString(R.string.preference_userId_key), -1).apply();

        int stored = prefs.getInt(context.getString(R.string.preference_userId_key), 123);

        assertEquals(-1, stored);
    }
}