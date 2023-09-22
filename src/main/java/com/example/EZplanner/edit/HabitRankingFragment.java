package com.example.EZplanner.edit;

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

import com.example.EZplanner.components.RankingItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import EZplanner.R;

public class HabitRankingFragment extends Fragment {

    private DatabaseReference databaseReference;
    private List<RankingItem> rankingItemList;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_habit_ranking, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rankingRecycler);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Habit Ranking");

        rankingItemList = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot user : snapshot.getChildren()) {
                        String name = user.child("username").getValue(String.class);
                        Long result = user.child("habit_result").getValue(Long.class);
                        if (result != null) {
                            rankingItemList.add(new RankingItem(name, Math.toIntExact(result)));
                        }
                    }
                    Collections.sort(rankingItemList);
                    Collections.reverse(rankingItemList);
                }
                HabitRankingAdapter adapter = new HabitRankingAdapter(rankingItemList);
                recyclerView.setAdapter(adapter);
                if (getParentFragment() != null) {
                    recyclerView.setLayoutManager(new LinearLayoutManager(getParentFragment().getContext()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
