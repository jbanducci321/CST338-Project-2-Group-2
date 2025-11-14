package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;

public class Statistics extends AppCompatActivity {
    private static final String STATS_ACTIVITY_USER_ID = "com.example.labandroiddemo.STATS_ACTIVITY_USER_ID";
    private static final int LOGGED_OUT = -1;

    private AppRepository repository;
    private User user;
    private int loggedInUserId = LOGGED_OUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        setSupportActionBar(findViewById(R.id.toolbar));

        //TODO STATS REPOSITORY repository  ;


        loggedInUserId = getIntent().getIntExtra(STATS_ACTIVITY_USER_ID, LOGGED_OUT);
        if (loggedInUserId == LOGGED_OUT) {
            SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
            loggedInUserId = sp.getInt(getString(R.string.preference_userId_key), LOGGED_OUT);
        }

        if (loggedInUserId != LOGGED_OUT) {
            LiveData<User> userObs = repository.getUserByUserId(loggedInUserId);
            userObs.observe(this, u -> {
                user = u;
                if (user != null) invalidateOptionsMenu();
            });
        }
    }

    // Menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem userItem = menu.findItem(R.id.logoutMenuItem);
        MenuItem adminItem = menu.findItem(R.id.admin);
        if (userItem == null || adminItem == null) return true;

        if (user == null) {
            userItem.setVisible(false);
            adminItem.setVisible(false);
            return true;
        }

        userItem.setVisible(true);
        View actionView = userItem.getActionView();
        if (actionView != null) {
            TextView usernameView = actionView.findViewById(R.id.usernameTitle);
            if (usernameView != null) usernameView.setText(user.getUsername());
        }

        adminItem.setVisible(user.isAdmin());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            logout();
            return true;
        } else if (id == R.id.stats) {
            // already here
            return true;
        } else if (id == R.id.admin) {
            Intent intent = new Intent(this, AdminLanding.class);
            intent.putExtra(STATS_ACTIVITY_USER_ID, loggedInUserId);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sp.edit().putInt(getString(R.string.preference_userId_key), LOGGED_OUT).apply();
        Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
        startActivity(intent);
        finish();
    }


    static Intent statsIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, Statistics.class);
        intent.putExtra(STATS_ACTIVITY_USER_ID, userId);
        return intent;
    }

}