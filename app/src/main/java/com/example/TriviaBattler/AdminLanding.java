package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
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
import com.google.android.material.appbar.MaterialToolbar;

public class AdminLanding extends AppCompatActivity {
    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = -LOGGED_OUT;

    private AppRepository repository;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_landing);

        //Menu things
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = AppRepository.getRepository(getApplication());

        int userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;
                if (user != null) invalidateOptionsMenu();
            });
        }
    }

    //Menu Inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    //Visibility of menu items
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem userItem = menu.findItem(R.id.logoutMenuItem);
        MenuItem adminItem = menu.findItem(R.id.admin);

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

    //Options for menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.logout) {
            startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            finish();
            return true;
        } else if (id == R.id.stats) {
            //Intent intent = Statistics.statsIntentFactory(this, loggedInUserId);
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    static Intent adminLandingIntentFactory(Context context){
        Intent intent =new Intent(context, AdminLanding.class);
        return intent;
    }
}