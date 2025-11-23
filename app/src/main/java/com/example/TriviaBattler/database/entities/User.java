package com.example.TriviaBattler.database.entities;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.TriviaBattler.database.AppDatabase;

import java.util.Objects;

@Entity(tableName = AppDatabase.USER_TABLE,
        indices = {@Index(value = {"username"}, unique = true)} //Ensures username must be unique and not null
)
public class User {

    @PrimaryKey(autoGenerate = true)
    private int userId;

    @NonNull
    private String username;

    private String password;

    private boolean isAdmin;

    public User(@NonNull String username, String password) {
        this.username = username;
        this.password = password;
        isAdmin = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && isAdmin == user.isAdmin && Objects.equals(username, user.username) && Objects.equals(password, user.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, username, password, isAdmin);
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}
