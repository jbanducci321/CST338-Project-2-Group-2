package com.example.labandroiddemo.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.labandroiddemo.MainActivity;
import com.example.labandroiddemo.database.daos.UserDAO;
import com.example.labandroiddemo.database.entities.User;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AppRepository {

    private final UserDAO userDAO;

    private static AppRepository repository;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
    }

    public static AppRepository getRepository(Application application) {
        if (repository != null) {
            return repository;
        }
        Future<AppRepository> future = AppDatabase.databaseWriteExecutor.submit(
                new Callable<AppRepository>() {
                    @Override
                    public AppRepository call() throws Exception {
                        return new AppRepository(application);
                    }
                }
        );
        try {
            return future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            Log.d(MainActivity.TAG, "Problem getting AppRepository, thread error");
        }
        return null;
    }

    public void insertUser(User... user) {
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }



}
