package com.example.EZplanner.fragments;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.EZplanner.HomeScreenActivity;
import com.example.EZplanner.components.TimerHistory;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import EZplanner.R;

public class TimerHistoryFragment extends Fragment {

    private List<TimerHistory> timerHistoryList;
    private RecyclerView recyclerView;
    private TimerHistoryAdapter timerHistoryAdapter;
    private DatabaseReference databaseReference;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_timer_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = view.findViewById(R.id.recycler_timerHistory);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Timer History");

        Query query = databaseReference.child("History").orderByChild("username").equalTo(HomeScreenActivity.username);

        timerHistoryList = new ArrayList<>();


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot history : snapshot.getChildren()) {
                        String location = history.child("location").getValue(String.class);
                        String timerTime = history.child("timer_time").getValue(String.class);
                        timerHistoryList.add(new TimerHistory(location, timerTime));
                    }
                }

                timerHistoryAdapter = new TimerHistoryAdapter(timerHistoryList);
                recyclerView.setAdapter(timerHistoryAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}