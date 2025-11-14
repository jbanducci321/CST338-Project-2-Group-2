package com.example.TriviaBattler.database;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.TriviaBattler.MainActivity;
import com.example.TriviaBattler.database.daos.UserDAO;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.database.typeConverters.LocalDateTypeConverter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@TypeConverters(LocalDateTypeConverter.class)
@Database(entities = {User.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public static final String DB_NAME = "app_db";
    public static final String USER_TABLE = "users";
    public static final String QUESTION_TABLE = "questions";
    private static final int NUMBER_OF_THREADS = 4;

    public abstract UserDAO userDAO();

    private static volatile AppDatabase INSTANCE;

    static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    Log.i(MainActivity.TAG, "Building database");
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class, DB_NAME)
                            .addCallback(addDefaultValues)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback addDefaultValues = new RoomDatabase.Callback() {

        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            Log.i(MainActivity.TAG, "Database Created");
            databaseWriteExecutor.execute(() -> {
                UserDAO dao = INSTANCE.userDAO();
                dao.deleteAll();
                User admin = new User("admin2", "admin2");
                admin.setAdmin(true);
                dao.insert(admin);

                User testUser1 = new User("testuser1", "testuser1");
                dao.insert(testUser1);
            });

        }

    };


}
