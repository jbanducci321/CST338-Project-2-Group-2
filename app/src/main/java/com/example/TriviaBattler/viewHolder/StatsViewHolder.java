package com.example.TriviaBattler.viewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.TriviaBattler.R;
import com.example.TriviaBattler.database.entities.Stats;

import java.util.List;

public class StatsViewHolder extends RecyclerView.ViewHolder {
    private final TextView textView;

    /**
     * Viewholder
     * @param itemView item to view
     */
    public StatsViewHolder(@NonNull View itemView) {
        super(itemView);
        textView = itemView.findViewById(R.id.statsRecyclerItemTextView);
    }

    /**
     * binding
     * @param text to view
     */
    public void bind(String text) {
        textView.setText(text);
    }

    /**
     * Create view holder
     * @param parent parent
     * @return view holder
     */
    static StatsViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.statistics_recycler_layout, parent, false);
        return new StatsViewHolder(view);
    }
}

