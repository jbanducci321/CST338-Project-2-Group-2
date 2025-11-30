package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityAddQuestionsAdminBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class AddQuestionsAdminActivity extends AppCompatActivity {
    private static final String ADD_QUESTION_ACTIVITY_USER_ID = "com.example.labandroiddemo.ADD_QUESTION_ACTIVITY_USER_ID";

    private static final int LOGGED_OUT = -1;
    private int userId = -LOGGED_OUT;
    private User user;
    private ActivityAddQuestionsAdminBinding binding;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionsAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar));


        repository = AppRepository.getRepository(getApplication());
        userId = getIntent().getIntExtra(ADD_QUESTION_ACTIVITY_USER_ID, -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;
                if (user != null) invalidateOptionsMenu();
            });
        }

        binding.buttonTempSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount = Integer.parseInt(binding.editTextTempNumberAmount.getText().toString().trim());
                String difficulty = binding.editTextTempDifficulty.getText().toString().trim().toLowerCase();

                repository.apiCall(difficulty, amount);
            }
        });

        binding.buttonTempBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), userId));
            }
        });

    }

    static Intent addQuestionIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, AddQuestionsAdminActivity.class);
        intent.putExtra(ADD_QUESTION_ACTIVITY_USER_ID, userId);
        return intent;
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
            Intent intent = Statistics.statsIntentFactory(this, userId);
            startActivity(intent);
            return true;
        }else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(), userId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    }