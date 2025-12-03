package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityAddAdminBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddAdmin extends AppCompatActivity {

    private static final String ADD_ADMIN_ACTIVITY_USER_ID = "com.example.labandroiddemo.ADD_ADMIN_ACTIVITY_USER_ID";
    private ActivityAddAdminBinding binding;
    private User user;
    private int userId;
    private AppRepository repository;
    private boolean newIsAdmin;

    private static final String MODIFY_ADMIN_ACTIVITY_NEW_BOOLEAN="com.example.labandroiddemo.MODIFY_ADMIN_ACTIVITY_USER_ID";

    /**
     * Create!
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityAddAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        repository = AppRepository.getRepository(getApplication());

        userId = getIntent().getIntExtra(ADD_ADMIN_ACTIVITY_USER_ID, -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;

                if (user != null){
                    userId=user.getUserId();
                    invalidateOptionsMenu();
                }
            });
        }
        binding.adminsTextView.setMovementMethod(new ScrollingMovementMethod());
        newIsAdmin=getIntent().getBooleanExtra(MODIFY_ADMIN_ACTIVITY_NEW_BOOLEAN,false);
        updateDisplayedUsers();
        binding.searchUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String search = binding.usernameEditText.getText().toString().toLowerCase();
                if (search.isEmpty()) {
                    toastMaker("Please enter a username");
                    binding.usernameEditText.setText("");
                    return;
                }else {
                    modifyAdmin(search);
                }

                binding.usernameEditText.setText("");
            }


        });

    }

    /**
     * display users
     */
    private void updateDisplayedUsers(){
        String listOfUsers="";
        List<User> repoList = repository.getAllUsers();
        if (newIsAdmin){
            listOfUsers="Non-Admins:\n";
            for(User u : repoList){
                if(!u.isAdmin())
                    listOfUsers=listOfUsers + u.getUsername()+"\n";
            }
            binding.adminsTextView.setText(listOfUsers);
        }
        if (!newIsAdmin){
            listOfUsers="Current Admins:\n";
            for(User u : repoList){
                if(u.isAdmin())
                    listOfUsers=listOfUsers+u.getUsername()+"\n";
            }
            binding.adminsTextView.setText(listOfUsers);
        }
    }

    /**
     * Toast
     * @param message a toast
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * admin intent
     * @param context a context
     * @param loggedInUserId user is logged in
     * @param newIsAdmin admin
     * @return intent
     */
    static Intent addAdminIntentFactory(Context context, int loggedInUserId, boolean newIsAdmin){
        Intent intent =new Intent(context, AddAdmin.class);
        intent.putExtra(ADD_ADMIN_ACTIVITY_USER_ID, loggedInUserId);
        intent.putExtra(MODIFY_ADMIN_ACTIVITY_NEW_BOOLEAN,newIsAdmin);
        return intent;
    }

    /**
     * Menu Inflater
     * @param menu The options menu in which you place your items.
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    /**
     * Visibility of menu items
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     *
     * @return true
     */
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

    /**
     * options on menu
     * @param item The menu item that was selected.
     *
     * @return true
     */
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
        }else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(),userId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * logout function
     */
    private void logout() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sp.edit().putInt(getString(R.string.preference_userId_key), -1).apply();
        Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
        startActivity(intent);
        finish();
    }

    /**
     * Admin or not
     * @param search for usernames
     */
    private void modifyAdmin(String search) {
        LiveData<User> observer = repository.getUserByUserName(search);
                    observer.observe(AddAdmin.this, u -> {
            if (u != null && u.getUserId() != userId) {
                repository.setAdmin(u.getUsername(), newIsAdmin);
                updateDisplayedUsers();
                toastMaker(String.format("%s is now a %s.", search.toLowerCase(), newIsAdmin ? "admin" : "non-admin"));
                binding.usernameEditText.setText("");
            } else if (u != null && u.getUserId() == userId) {
                toastMaker("Cannot modify current user.");
                binding.usernameEditText.setText("");
            } else {
                toastMaker(String.format("%s is not a valid username.", search));
                binding.usernameEditText.setText("");
            }
            observer.removeObservers(AddAdmin.this);
        });

    }

}