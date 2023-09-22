package com.example.EZplanner.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import EZplanner.R;
import com.example.EZplanner.components.Habit;

import java.util.ArrayList;
import java.util.List;

public class HabitAdapter extends RecyclerView.Adapter<HabitAdapter.ViewHolder>{

    /*
    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }

     */

    public interface OnCheckedChangeListener {
        void onCheckedChanged(int position);
    }

    public interface OnDeleteButtonClickListener {
        void onDeleteButtonClicked(int position);
    }

    public interface OnUncheckAllButtonClickListener {
        void onUncheckAllButtonClicked();
    }

    List<Habit> items;
    //OnLongClickListener longClickListener;
    OnCheckedChangeListener checkedChangeListener;
    OnDeleteButtonClickListener deleteButtonClickListener;
    OnUncheckAllButtonClickListener uncheckAllButtonClickListener;


    public HabitAdapter(List<String> strItems, OnDeleteButtonClickListener deleteButtonClickListener,
                        OnCheckedChangeListener checkedChangeListener) {
        this.items = parseItems(strItems);
        this.checkedChangeListener = checkedChangeListener;
        this.deleteButtonClickListener = deleteButtonClickListener;
        this.uncheckAllButtonClickListener = uncheckAllButtonClickListener;
    }

    public void bind(List<String> items) {
        this.items = parseItems(items);
        notifyDataSetChanged();
    }

    // helper function parse string items into list of objects
    public List<Habit> parseItems(List<String> strItems) {
//        Log.i("tag", strItems.toString());
        List<Habit> itms = new ArrayList<>();
        for (String strItem : strItems) {
            String[] arr = strItem.split(",");
            boolean isCompleted = arr[0].equals("true") ? true : false;
            String habit = arr[1];
            Habit item = new Habit();
            item.setHabit(habit);
            item.setCompleted(isCompleted);
            itms.add(item);
        }
        return itms;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //use layout inflater to inflate a view
        View habitView = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_item, parent, false);
        return new ViewHolder(habitView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit item = items.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    //container to provide easy access to views that represent each row of the list

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox isCompleted;
        TextView textHabit;
        Button delete;

        public ViewHolder (View itemView) {
            super (itemView);
            isCompleted = itemView.findViewById(R.id.isCompleted);
            textHabit = itemView.findViewById(R.id.textHabit);
            delete = itemView.findViewById(R.id.btnDelete);
        }

        //Update the view inside of the view with this data
        public void bind(Habit item) {
            textHabit.setText(item.getHabit());

            isCompleted.setOnCheckedChangeListener(null);

            isCompleted.setChecked(item.isCompleted());
            isCompleted.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    checkedChangeListener.onCheckedChanged(getAdapterPosition());

                }
            });

            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteButtonClickListener.onDeleteButtonClicked(getAdapterPosition());
                }
            });

            /*
            textHabit.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // remove the habit from the recycler view
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });

             */
        }
    }

}
