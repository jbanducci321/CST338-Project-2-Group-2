package com.example.TriviaBattler;

import static org.junit.Assert.*;

import android.content.Context;
import android.os.Build;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.daos.UserDAO;
import com.example.TriviaBattler.database.daos.StatsDAO;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.database.entities.Stats;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, sdk = Build.VERSION_CODES.P)
public class StatsDaoTest {

    private AppDatabase db;
    private UserDAO userDao;
    private StatsDAO statsDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDAO();
        statsDao = db.statsDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeStats() {
        User user = new User("statsUser", "password");
        int userId = 42;
        user.setUserId(userId);
        userDao.insert(user);

        Stats stats = new Stats(userId);
        stats.setCorrectCount(3);
        stats.setWrongCount(2);
        stats.setTotalCount(5);
        stats.setOverallScore(60.0);

        statsDao.insert(stats);

        Stats loaded = statsDao.getByUserId(userId);

        assertNotNull(loaded);
        assertEquals(userId, loaded.getUserId());
        assertEquals(3, loaded.getCorrectCount());
        assertEquals(2, loaded.getWrongCount());
        assertEquals(5, loaded.getTotalCount());
        assertEquals(60.0, loaded.getOverallScore(), 0.001);
    }
}