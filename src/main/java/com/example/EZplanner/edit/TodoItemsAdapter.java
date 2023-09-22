package com.example.EZplanner.edit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import EZplanner.R;

public class TodoItemsAdapter extends RecyclerView.Adapter<TodoItemsAdapter.ViewHolder>{

    /*
    public interface OnLongClickListener {
        void onItemLongClicked(int position);
    }
     */

    public interface OnDoneButtonClickListener {
        void onDoneButtonClicked(int position);
    }

    List<String> items;
    //OnLongClickListener longClickListener;
    OnDoneButtonClickListener doneButtonClickListener;

    public TodoItemsAdapter(List<String> items, OnDoneButtonClickListener doneButtonClickListener) {
        this.items = items;
        this.doneButtonClickListener = doneButtonClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //use layout inflater to inflate a view
        View todoView = LayoutInflater.from(parent.getContext()).inflate(R.layout.todo_item, parent, false);
        return new ViewHolder(todoView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String item = items.get(position);
        holder.bind(item);

    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    //container to provide easy access to views that represent each row of the list

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvItem;
        Button done;

        public ViewHolder (View itemView) {
            super (itemView);
            tvItem = itemView.findViewById(R.id.todo_item);
            done = itemView.findViewById(R.id.btnDoneOrDelete);
        }

        //Update the view inside of the view with this data
        public void bind(String item) {
            tvItem.setText(item);
//            tvItem.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    clickListener.onItemClicked(getAdapterPosition());
//                }
//
//            });

            /*
            tvItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    //remove the item from the recycler view
                    longClickListener.onItemLongClicked(getAdapterPosition());
                    return true;
                }
            });
             */

            done.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    doneButtonClickListener.onDoneButtonClicked(getAdapterPosition());
                }
            });

        }
    }

}
