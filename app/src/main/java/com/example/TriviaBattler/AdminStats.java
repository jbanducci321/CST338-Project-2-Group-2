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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityAddAdminBinding;
import com.example.TriviaBattler.databinding.ActivityAdminStatsBinding;
import com.example.TriviaBattler.viewHolder.StatisticsViewModel;
import com.example.TriviaBattler.viewHolder.StatsAdapter;
import com.google.android.material.appbar.MaterialToolbar;

public class AdminStats extends AppCompatActivity {
    private static final String ADMIN_STATS_ACTIVITY_USER_ID = "com.example.labandroiddemo.ADMIN_STATS_ACTIVITY_USER_ID";
    private ActivityAdminStatsBinding binding;
    private User user;
    private Integer userId;
    private AppRepository repository;
    private StatisticsViewModel statisticsViewModel;

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
        binding= ActivityAdminStatsBinding.inflate(getLayoutInflater());
        repository = AppRepository.getRepository(getApplication());
        setContentView(binding.getRoot());

        statisticsViewModel = new ViewModelProvider(this).get(StatisticsViewModel.class);
        RecyclerView recyclerView = binding.allStatsRecyclerView;
        final StatsAdapter adapter = new StatsAdapter(new StatsAdapter.StatsDiff(),getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        userId = getIntent().getIntExtra(ADMIN_STATS_ACTIVITY_USER_ID, -1);
        if (userId != -1) {
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;

                if (user != null) invalidateOptionsMenu();
            });
        }

        statisticsViewModel.getAllStatsLive().observe(this, adapter::submitList);

    }

    /**
     * intent factory
     * @param context context
     * @param loggedInUserId logged in user
     * @return abstract idea of intent
     */
    static Intent adminStatsIntentFactory(Context context, int loggedInUserId){
        Intent intent =new Intent(context, AdminStats.class);
        intent.putExtra(ADMIN_STATS_ACTIVITY_USER_ID, loggedInUserId);
        return intent;
    }

    /**
     * menu inflater
     * @param menu The options menu in which you place your items.
     *
     * @return true or false
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    /**
     * visibility for menu
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     *
     * @return true or false
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
     * Options for menu
     * @param item The menu item that was selected.
     *
     * @return true or false
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
        }else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(),userId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}