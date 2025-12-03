package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

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

    /**
     * Buttons and Create
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionsAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        repository = AppRepository.getRepository(getApplication());
        userId = getIntent().getIntExtra(ADD_QUESTION_ACTIVITY_USER_ID, -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;
                if (user != null) {
                    invalidateOptionsMenu();
                }
            });
        }

        binding.buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int amount;
                //Makes sure the entered number won't cause errors for the api call
                try {
                    amount = Integer.parseInt(binding.editTextNumberAmount.getText().toString().trim());
                    if (amount <= 0) {
                        toastMaker("Enter a number greater than 0");
                        return;
                    }
                } catch (NumberFormatException e) {
                    toastMaker("Enter a valid number");
                    return;
                }

                //Selects the difficulty of the questions being called
                String difficulty = "easy"; //Default value for the radio buttons
                int checkedButton = binding.radioGroupDifficulty.getCheckedRadioButtonId();
                if (checkedButton == binding.rbMedium.getId()) {
                    difficulty = "medium";
                } else if (checkedButton == binding.rbHard.getId()) {
                    difficulty = "hard";
                }

                //Collects the category from the spinner
                int spinnerPos = binding.spinnerCategory.getSelectedItemPosition();
                int categoryId = mapCategoryPositionToId(spinnerPos);

                boolean status = repository.apiCallAdminCategory(difficulty, amount, categoryId);

                if (status) {
                    toastMaker("Questions added successfully");
                } else {
                    toastMaker("Error adding question, try again");
                }

            }
        });

    }

    /**
     * toast
     * @param msg very toasty
     */
    private void toastMaker(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    /**
     * Intent Factory
     * @param context context
     * @param userId a users id
     * @return intent
     */
    static Intent addQuestionIntentFactory(Context context, int userId) {
        Intent intent = new Intent(context, AddQuestionsAdminActivity.class);
        intent.putExtra(ADD_QUESTION_ACTIVITY_USER_ID, userId);
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
     * options for menu
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
            startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            finish();
            return true;
        } else if (id == R.id.stats) {
            Intent intent = Statistics.statsIntentFactory(this, userId);
            startActivity(intent);
            return true;
        } else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(), userId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Helper method that maps the location of the text to its api category id
     * @param position a number
     * @return a category
     */
    private int mapCategoryPositionToId(int position) {
        switch (position) {
            case 0:
                return 0;   // Any Category
            case 1:
                return 9;   // General Knowledge
            case 2:
                return 10;  // Entertainment: Books
            case 3:
                return 11;  // Entertainment: Film
            case 4:
                return 12;  // Entertainment: Music
            case 5:
                return 13;  // Entertainment: Musicals & Theatres
            case 6:
                return 14;  // Entertainment: Television
            case 7:
                return 15;  // Entertainment: Video Games
            case 8:
                return 16;  // Entertainment: Board Games
            case 9:
                return 17;  // Science & Nature
            case 10:
                return 18;  // Science: Computers
            case 11:
                return 19;  // Science: Mathematics
            case 12:
                return 20;  // Mythology
            case 13:
                return 21;  // Sports
            case 14:
                return 22;  // Geography
            case 15:
                return 23;  // History
            case 16:
                return 24;  // Politics
            case 17:
                return 25;  // Art
            case 18:
                return 26;  // Celebrities
            case 19:
                return 27;  // Animals
            case 20:
                return 28;  // Vehicles
            case 21:
                return 29;  // Entertainment: Comics
            case 22:
                return 30;  // Science: Gadgets
            case 23:
                return 31;  // Entertainment: Japanese Anime & Manga
            case 24:
                return 32;  // Entertainment: Cartoon & Animations
            default:
                return 0;   // Fallback to Any Category
        }

    }
}