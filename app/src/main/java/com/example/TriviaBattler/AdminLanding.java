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
import com.example.TriviaBattler.databinding.ActivityAdminLandingBinding;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminLanding extends AppCompatActivity {

    private static final String ADMIN_ACTIVITY_USER_ID = "com.example.labandroiddemo.ADMIN_ACTIVITY_USER_ID";

    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = -LOGGED_OUT;

    private int userId;
    private AppRepository repository;
    private User user;
    private ActivityAdminLandingBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityAdminLandingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //Menu things
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = AppRepository.getRepository(getApplication());

        userId = getIntent().getIntExtra(ADMIN_ACTIVITY_USER_ID, -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;
                binding.adminUser.setText(user.getUsername());
                if (user != null) {
                    userId = user.getUserId();
                    invalidateOptionsMenu();
                }
            });
        }

        binding.addAdminButton.setOnClickListener(v -> {

            startActivity(AddAdmin.addAdminIntentFactory(
                    getApplicationContext(),
                    userId,true
            ));
        });
        binding.removeAdminButton.setOnClickListener(v -> {

            startActivity(AddAdmin.addAdminIntentFactory(
                    getApplicationContext(),
                    userId,false
            ));
        });
        binding.editStatistics.setOnClickListener(v -> {

            startActivity(AdminStats.adminStatsIntentFactory(
                    getApplicationContext(),
                    userId
            ));
        });

        binding.buttonAddQuestions.setOnClickListener(v -> {
            startActivity(AddQuestionsAdminActivity.addQuestionIntentFactory(
                    getApplicationContext(),
                    userId
            ));
        });

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
            logout();
            return true;
        } else if (id == R.id.stats) {
            Intent intent = Statistics.statsIntentFactory(this, userId);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    static Intent adminLandingIntentFactory(Context context, int loggedInUserId){
        Intent intent =new Intent(context, AdminLanding.class);
        intent.putExtra(ADMIN_ACTIVITY_USER_ID, loggedInUserId);
        return intent;
    }
    private void logout() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sp.edit().putInt(getString(R.string.preference_userId_key), LOGGED_OUT).apply();
        Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
        startActivity(intent);
        finish();
    }
}