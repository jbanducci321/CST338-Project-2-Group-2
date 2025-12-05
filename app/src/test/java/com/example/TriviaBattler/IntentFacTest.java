package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class IntentFacTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void containsExtras() {
        int userId = 10;
        String difficulty = "easy";

        Intent i = QuestionsActivity.questionsIntentFactory(context, userId, difficulty);

        assertEquals(QuestionsActivity.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("USER_ID", -1));
        assertEquals(difficulty, i.getStringExtra("EXTRA_DIFFICULTY"));
    }

    @Test
    public void containsUserId() {
        int userId = 77;

        Intent i = Statistics.statsIntentFactory(context, userId);

        assertEquals(Statistics.class.getName(), i.getComponent().getClassName());
        assertEquals(userId, i.getIntExtra("com.example.labandroiddemo.STATS_ACTIVITY_USER_ID", -1));
    }
}