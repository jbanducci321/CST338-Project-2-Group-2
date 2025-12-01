package com.example.TriviaBattler.viewHolder;


import android.app.Application;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.Stats;
import com.example.TriviaBattler.database.entities.User;

import java.util.ArrayList;

public class StatsAdapter extends ListAdapter<Stats, StatsViewHolder> {
    private AppRepository repository;
    public StatsAdapter(@NonNull DiffUtil.ItemCallback<Stats> diffCallback,Application application) {
        super(diffCallback);
        this.repository = AppRepository.getRepository(application);
    }

    @Override
    public StatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return StatsViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull StatsViewHolder holder, int position) {
        Stats current = getItem(position);

        // Fetch the user asynchronously. The result is handled in the callback.
        repository.getUserByUserIdNotLive(current.getUserId(), user -> {
            if (user != null) {
                String username = user.getUsername();
                // Now that you have the username, update the ViewHolder.
                // This will run on the main thread if your repository is set up correctly.
                holder.bind(username + "\n\t" + current.toString());
            } else {
                // Handle the case where the user might not be found
                holder.bind("Unknown User\n\t" + current.toString());
            }
        });
    }

    public static class StatsDiff extends DiffUtil.ItemCallback<Stats> {
        @Override
        public boolean areContentsTheSame(@NonNull Stats oldItem, @NonNull Stats newItem) {
            return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(@NonNull Stats oldItem, @NonNull Stats newItem) {
            return oldItem==newItem;
        }
    }

}
