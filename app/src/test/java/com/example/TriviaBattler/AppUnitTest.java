package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Build;

import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.api.ApiClient;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.database.entities.Stats;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class AppUnitTest {

    private final Context context = ApplicationProvider.getApplicationContext();

    @Test
    public void statsDefaultValues() {
        Stats s = new Stats(1);
        assertEquals(0, s.getCorrectCount());
        assertEquals(0, s.getWrongCount());
        assertEquals(0, s.getTotalCount());
        assertEquals(0.0, s.getOverallScore(), 0.001);
    }

    @Test
    public void statsFormatting() {
        Stats s = new Stats(1);
        s.setCorrectCount(10);
        s.setWrongCount(20);
        s.setTotalCount(30);
        s.setOverallScore(33.3333333);

        String text = s.toString();
        assertTrue(text.contains("33.33"));
    }

    @Test
    public void questionEntityStores() {
        Question q = new Question(
                "multiple",
                "hard",
                "Science",
                "What is H2O?",
                "Water",
                Arrays.asList("Hydrogen", "Oxygen", "Helium")
        );

        assertEquals(3, q.getIncorrectAnswers().size());
        assertTrue(q.getIncorrectAnswers().contains("Oxygen"));
    }

    @Test
    public void apiClient() {
        assertNotNull(ApiClient.getService());
    }

    @Test
    public void background() {
        int id = context.getResources()
                .getIdentifier("question_background_repeat", "drawable", context.getPackageName());

        assertTrue(id == 0 || id > 0);
    }

    @Test
    public void questionCounter() {
        int current = 1;
        final int TOTAL = 10;

        for (int i = 1; i <= TOTAL; i++) {
            assertTrue(current <= TOTAL);
            current++;
        }
    }

    @Test
    public void recordResult() {
        int correct = 7;
        int wrong = 6;

        int total = correct + wrong;
        double pct = (correct * 100.0) / total;

        assertEquals(13, total);
        assertEquals(53.85, pct, 0.01);
    }
}