package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.LiveData;

import com.bumptech.glide.Glide;
import com.example.TriviaBattler.R;
import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private static final String MAIN_ACTIVITY_USER_ID = "com.example.labandroiddemo.MAIN_ACTIVITY_USER_ID";
    static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.labandroiddemo.SAVED_INSTANCE_STATE_USERID_KEY";
    private static final int LOGGED_OUT = -1;
    private ActivityMainBinding binding;
    private AppRepository repository;

    public static final String EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY";

    private int loggedInUserId = LOGGED_OUT;
    private User user;

    public static final String TAG = "DAC_APP";

    String preference_file_key = "preference_file_key";

    /***
     * Checks if user is logged-in. If not, starts Login Activity.<br>
     * Else, set up toolbar, Option Menu dropdown, and button on clickers.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .asGif()
                .load(R.drawable.riddlemethisbatman)
                .into(binding.imageViewGif);

        //start menu
        setSupportActionBar(findViewById(R.id.toolbar));

        repository = AppRepository.getRepository(getApplication());

        loginUser(savedInstanceState);

        // If not logged in, go to Login (same behavior you see in GymLog when session missing)
        if (loggedInUserId == LOGGED_OUT) {
            startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            finish();
            return;
        }

        updateSharedPreference();

        // Button on click listeners
        binding.easyQuestionsButton.setOnClickListener(v -> {
            repository.apiCall("easy", 10);

            startActivity(QuestionsActivity.questionsIntentFactory(
                    getApplicationContext(),
                    loggedInUserId,
                    "easy"
            ));
        });

        binding.normalQuestionsButton.setOnClickListener(v -> {
            repository.apiCall("medium", 10);

            startActivity(QuestionsActivity.questionsIntentFactory(
                    getApplicationContext(),
                    loggedInUserId,
                    "medium"
            ));
        });

        binding.hardQuestionsButton.setOnClickListener(v -> {
            repository.apiCall("hard", 10);

            startActivity(QuestionsActivity.questionsIntentFactory(
                    getApplicationContext(),
                    loggedInUserId,
                    "hard"
            ));
        });

    }

    /***
     * Menu inflater.
     * @param menu The options menu in which you place your items.
     *
     * @return true
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }


    /***
     * Prepares the Options Menu dropdown selection depending on the logged-in user.
     *
     * @param menu The options menu as last shown or first initialized by onCreateOptionsMenu().
     * @return true
     */
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
            if (usernameView != null) {
                usernameView.setText(user.getUsername());
            }
        }

        adminItem.setVisible(user.isAdmin());

        return super.onPrepareOptionsMenu(menu);
    }

    /***
     * Processes Options Menu dropdown selection.
     *
     * @param item The menu item that was selected.
     * @return false to allow normal menu processing to proceed, true to consume it here.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.logout) {
            logout();
            return true;
        }
        else if (id == R.id.stats) {
            Intent intent = Statistics.statsIntentFactory(this, loggedInUserId);
            startActivity(intent);
            return true;
        }
        else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(),loggedInUserId));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /***
     * Sets up the user role text and greets the user.
     * @param savedInstanceState optional state bundle containing a previously saved user ID may be {@code null}.
     */
    public void loginUser(Bundle savedInstanceState) {
        // === GymLog-style userId retrieval order: SharedPrefs -> savedInstance -> Intent ===
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        loggedInUserId = sharedPreferences.getInt(
                getString(R.string.preference_userId_key), LOGGED_OUT);

        if (loggedInUserId == LOGGED_OUT && savedInstanceState != null
                && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)) {
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }

        if (loggedInUserId == LOGGED_OUT) {
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }
        // === Observe the logged-in user (GymLog pattern) ===
        LiveData<User> userObserver = repository.getUserByUserId(loggedInUserId);
        userObserver.observe(this, u -> {
            user = u;
            if (user != null) {
                invalidateOptionsMenu();
                // Update the role text exactly when user arrives/changes
                String role = user.isAdmin() ? "Admin" : "User";
                String text = "Logged in as: " + user.getUsername() + "\nRole: " + role;
                //binding.roleTextView.setText(text);
                binding.welcomeUserTextView.setText("Welcome\n"+user.getUsername()+"!");

            }
        });
    }

    /***
     * Logs out the currently logged-in user.<br>
     * Goes back to the Login Activity.
     */
    public void logout() {
        loggedInUserId = LOGGED_OUT;
        updateSharedPreference();
        getIntent().putExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
        finish();
    }


    /***
     * Saves activity state.
     *
     * @param outState Bundle in which to place your saved state.
     */
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_INSTANCE_STATE_USERID_KEY, loggedInUserId);
        updateSharedPreference();
    }

    /***
     * Updates shared preferences.
     */
    private void updateSharedPreference() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPrefEditor = sharedPreferences.edit();
        sharedPrefEditor.putInt(getString(R.string.preference_userId_key), loggedInUserId);
        sharedPrefEditor.apply();
    }

    /***
     * Creates a new Intent for the Main Activity.
     *
     * @param context the application context
     * @param userId the logged-in user's ID
     * @return the new Intent
     */
    static Intent mainActivityIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra(MAIN_ACTIVITY_USER_ID, userId);
        return intent;
    }


    /*
    public static class UserLandingPage extends AppCompatActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_user_landing_page);
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });
        }
    }
    */
}