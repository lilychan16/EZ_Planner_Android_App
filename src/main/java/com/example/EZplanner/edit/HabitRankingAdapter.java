package com.example.EZplanner.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.EZplanner.components.RankingItem;

import java.util.List;

import EZplanner.R;

public class HabitRankingAdapter extends RecyclerView.Adapter<HabitRankingAdapter.ViewHolder>{

    List<RankingItem> rankingItemList;

    public HabitRankingAdapter(List<RankingItem> rankingItemList) {
        this.rankingItemList = rankingItemList;
    }

    @NonNull
    @Override
    public HabitRankingAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.ranking_item, parent, false);
        return new HabitRankingAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitRankingAdapter.ViewHolder holder, int position) {
        RankingItem item = rankingItemList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return rankingItemList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, result;

        public ViewHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.userNameRanking);
            result = itemView.findViewById(R.id.userHabitRanking);
        }

        //Update the view inside of the view with this data
        public void bind(RankingItem item) {
            name.setText(item.getName());
            result.setText(String.valueOf(item.getResult()));
        }
    }
}
