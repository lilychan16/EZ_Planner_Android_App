package com.example.EZplanner.fragments;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.EZplanner.components.TimerHistory;

import java.util.List;

import EZplanner.R;

public class TimerHistoryAdapter extends RecyclerView.Adapter<TimerHistoryAdapter.ViewHolder>{

    List<TimerHistory> items;

    public TimerHistoryAdapter(List<TimerHistory> items) {
        this.items = items;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View timerHistoryView = LayoutInflater.from(parent.getContext()).inflate(R.layout.timer_history_item, parent, false);
        return new ViewHolder(timerHistoryView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TimerHistory item = items.get(position);
        holder.textLocation.setText(item.getLocation());
        holder.textTime.setText(item.getTime());
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView textLocation;
        TextView textTime;

        public ViewHolder (View itemView) {
            super (itemView);
            textLocation = itemView.findViewById(R.id.textTimerLocation);
            textTime = itemView.findViewById(R.id.textTimerTime);
        }
    }
}
