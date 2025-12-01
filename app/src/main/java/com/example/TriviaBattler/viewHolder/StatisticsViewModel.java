package com.example.TriviaBattler.viewHolder;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.Stats;
import com.example.TriviaBattler.database.entities.User;

import java.util.ArrayList;
import java.util.List;

public class StatisticsViewModel extends AndroidViewModel {
    private final AppRepository repository;



    public StatisticsViewModel(@NonNull Application application) {
        super(application);
        repository = AppRepository.getRepository(application);
    }
    public LiveData<List<Stats>> getAllStatsLive() {
        return repository.getAllStatsLive();
    }




    public void insertUser(User user) {
        repository.insertUser(user);
    }



}
