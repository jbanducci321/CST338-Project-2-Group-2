package com.example.TriviaBattler;

import static android.provider.SyncStateContract.Helpers.insert;
import static org.junit.Assert.*;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.daos.UserDAO;
import com.example.TriviaBattler.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@RunWith(RobolectricTestRunner.class)
@Config(sdk=28)
public class userDaoTest {
    private AppDatabase db;
    private UserDAO userDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        userDao = db.userDAO();
    }

    @After
    public void closeDb() {
        db.close();
    }

    @Test
    public void writeUser_andReadBack() {
        String username = "testuser";
        String password = "password";

        User user = new User(username, password);
        userDao.insert(user);
        //insert(User user);

        LiveData<User> liveUser = userDao.getUserByUserName(username);

        liveUser.observeForever(new Observer<User>() {
            @Override
            public void onChanged(User value) {
                if (value != null) {
                    assertEquals(username, value.getUsername());
                    assertEquals(password, value.getPassword());
                    assertTrue(value.getUserId() > 0);
                    liveUser.removeObserver(this);
                }
            }
        });
    }
}