package com.example.TriviaBattler.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.entities.User;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface UserDAO {

    /**
     * SQL for User DAO
     * @param user user
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (User... user);

    @Delete
    void delete (User user);

    @Query("DELETE FROM " + AppDatabase.USER_TABLE)
    void deleteAll();

    @Query("SELECT * from " + AppDatabase.USER_TABLE + " WHERE username == :username")
    LiveData<User> getUserByUserName(String username);

    @Query("SELECT * from " + AppDatabase.USER_TABLE + " WHERE userId == :userId")
    User getUserByUserIdNotLive(int userId);
    @Query("SELECT * from " + AppDatabase.USER_TABLE + " WHERE userId == :userId")
    LiveData<User> getUserByUserId(int userId);

    //Sets a user, by their username, to an admin (allows for demotion as well)
    @Query("UPDATE " + AppDatabase.USER_TABLE + " SET isAdmin = :isAdmin WHERE username = :username")
    Void setAdminByUsername(String username, boolean isAdmin); //TODO: May want to have it return something to verify operation successful


    //Checks if a username already exists (for account creation logic)
    @Query("SELECT EXISTS(SELECT 1 FROM " + AppDatabase.USER_TABLE + " WHERE username = :username)")
    boolean checkUsername(String username);

    @Query("SELECT * from " + AppDatabase.USER_TABLE)
    List<User> getAllUsers();




}
