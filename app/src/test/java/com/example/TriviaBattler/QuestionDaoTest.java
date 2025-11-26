package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Build;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.daos.QuestionDAO;
import com.example.TriviaBattler.database.entities.Question;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class QuestionDaoTest {

    private AppDatabase db;
    private QuestionDAO questionDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        questionDao = db.questionDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeQuestion() {
        String difficulty = "easy";

        List<String> incorrect = Arrays.asList("1", "2", "3");

        Question q = new Question(
                "multiple",
                difficulty,
                "Math",
                "What is 2 + 2?",
                "4",
                incorrect
        );

        questionDao.insert(q);

        LiveData<Question> liveQ = questionDao.getRandomByDifficulty(difficulty);

        liveQ.observeForever(new Observer<Question>() {
            @Override
            public void onChanged(Question value) {
                if (value != null) {
                    assertEquals("multiple", value.getType());
                    assertEquals(difficulty, value.getDifficulty());
                    assertEquals("Math", value.getCategory());
                    assertEquals("What is 2 + 2?", value.getQuestion());
                    assertEquals("4", value.getCorrectAnswer());
                    assertEquals(incorrect, value.getIncorrectAnswers());
                    liveQ.removeObserver(this);
                }
            }
        });
    }
}