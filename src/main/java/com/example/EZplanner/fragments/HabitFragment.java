package com.example.EZplanner.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EZplanner.HomeScreenActivity;
import EZplanner.R;
import com.example.EZplanner.edit.HabitAdapter;
import com.example.EZplanner.edit.HabitEditFragment;
import com.example.EZplanner.edit.HabitRankingFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HabitFragment extends Fragment {

    List<String> items;
    List<String> checkedItems;

    Button btnAdd;
    Button btnRanking;
    CheckBox check;
    Button btnUncheckAll;

    RecyclerView rvItem;
    HabitAdapter habitAdapter;
    ProgressBar bar;
    TextView text_progress;

    DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_habit_main, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        btnAdd = view.findViewById(R.id.btnAdd);
        btnRanking = view.findViewById(R.id.btnRanking);
        rvItem = view.findViewById(R.id.rvItem);
        check = view.findViewById(R.id.isCompleted);

        btnUncheckAll = view.findViewById(R.id.btnUncheckAll);

        bar = view.findViewById(R.id.progress_bar);
        text_progress = view.findViewById(R.id.text_view_progress);

        loadItems();
        checkedItems = new ArrayList<>();
        checkedItems = items.stream().filter(str -> str.startsWith("true"))
                .collect(Collectors.toList());

        if (items.size() == 0 || checkedItems.size() == 0) {
            bar.setProgress(0);
            text_progress.setText(String.valueOf(0));
        }
        else {
            updateProgressBar();
        }

        /*
        HabitAdapter.OnLongClickListener onLongClickListener = new HabitAdapter.OnLongClickListener() {
            public void onItemLongClicked(int position) {
                items.remove(position);
//                habitAdapter.notifyItemRemoved(position);
                habitAdapter.bind(items);
                Toast.makeText(getContext(), "Items was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };

         */

        btnUncheckAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (int i = 0; i < items.size(); i++) {
                    String h = items.get(i);
                    String[] strArr = h.split(",");
                    strArr[0] = strArr[0].equals("true") ? "false" : "false";
                    items.set(i, strArr[0] + "," + strArr[1]);
                }
                checkedItems = items.stream().filter(str -> str.startsWith("true"))
                        .collect(Collectors.toList());
                updateProgressBar();
                saveItems();
                habitAdapter.bind(items);
            }
        });

        btnRanking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment f = new HabitRankingFragment();
                HomeScreenActivity.isHabitRankingShown = true;
                HomeScreenActivity.isHabitMainShown = false;
                System.out.println("After enter ranking, the HabitMain is: " + HomeScreenActivity.isHabitMainShown);
                System.out.println("After enter ranking, the HabitRanking is: " + HomeScreenActivity.isHabitRankingShown);
                ((HomeScreenActivity) getActivity()).navigateToRanking(f);
            }
        });

        /*
        HabitAdapter.OnUncheckAllButtonClickListener uncheckAllButtonClickListener = new HabitAdapter.OnUncheckAllButtonClickListener() {
            @Override
            public void onUncheckAllButtonClicked() {
                for (int i = 0; i < items.size(); i++) {
                    String h = items.get(i);
                    String[] strArr = h.split(",");
                    strArr[0] = strArr[0].equals("true") ? "false" : "false";
                    items.set(i, strArr[0] + "," + strArr[1]);
                }
                checkedItems = items.stream().filter(str -> str.startsWith("true"))
                        .collect(Collectors.toList());
                updateProgressBar();
                saveItems();
                habitAdapter.bind(items);
            }
        };

         */

        HabitAdapter.OnDeleteButtonClickListener deleteButtonClickListener = new HabitAdapter.OnDeleteButtonClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onDeleteButtonClicked(int position) {
                items.remove(position);
                System.out.println("current items list after deletion: "+ items.toString());
                checkedItems = items.stream().filter(str -> str.startsWith("true"))
                        .collect(Collectors.toList());
                System.out.println("current checked items list after deletion: " + checkedItems.toString());
                updateProgressBar();
                saveItems();
                habitAdapter.bind(items);
//                habitAdapter.notifyItemRemoved(position)
                Toast.makeText(getContext(), "Items was removed", Toast.LENGTH_SHORT).show();
            }
        };

        HabitAdapter.OnCheckedChangeListener checkedChangeListener = new HabitAdapter.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            public void onCheckedChanged(int position) {
                    String h = items.get(position);
                    String[] strArr = h.split(",");
                    strArr[0] = strArr[0].equals("true") ? "false" : "true";
                    items.set(position, strArr[0] + "," + strArr[1]);
                    System.out.println("current items list after check: "+ items.toString());
                    checkedItems = items.stream().filter(str -> str.startsWith("true"))
                        .collect(Collectors.toList());
                    System.out.println("current checked items list after check: " + checkedItems.toString());
                    updateProgressBar();
    //                habitAdapter.notifyItemChanged(position);
                    Toast.makeText(getContext(), "Habit was checked", Toast.LENGTH_SHORT).show();
                    saveItems();
            }
        };


        habitAdapter = new HabitAdapter(items, deleteButtonClickListener, checkedChangeListener);
        rvItem.setAdapter(habitAdapter);
        rvItem.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // helper function written in MainActivity
                Fragment f = new HabitEditFragment();
                HomeScreenActivity.isHabitAddShown = true;
                HomeScreenActivity.isHabitMainShown = false;
                System.out.println("After enter add, the HabitMain is: " + HomeScreenActivity.isHabitMainShown);
                System.out.println("After enter add, the HabitAdd is: " + HomeScreenActivity.isHabitAddShown);
                ((HomeScreenActivity) getActivity()).navigateToEdit(f);
            }
        });
    }

    private void updateProgressBar() {
        int itemListSize = items.size();
        int checkedItemListSize = checkedItems.size();

        if (itemListSize == 0 || checkedItemListSize == 0) {
            bar.setProgress(0);
            text_progress.setText(String.valueOf(0));
            databaseReference.child("Users").child(HomeScreenActivity.username)
                    .child("habit_result").setValue(0);
            return;
        }

        int percentageInput = checkedItemListSize * 100 / itemListSize;
        bar.setProgress(percentageInput);
        text_progress.setText(String.valueOf(percentageInput));
        databaseReference.child("Users").child(HomeScreenActivity.username)
                .child("habit_result").setValue(percentageInput);
    }

    private File getDataFile () {
        return new File(getContext().getFilesDir(), HomeScreenActivity.username+"_habit.txt");
    }

    private void loadItems() {
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }



}