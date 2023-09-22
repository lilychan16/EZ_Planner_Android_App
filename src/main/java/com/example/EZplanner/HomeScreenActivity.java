package com.example.EZplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.MenuItem;
import android.widget.Toast;


import com.example.EZplanner.edit.HabitEditFragment;
import com.example.EZplanner.edit.HabitRankingFragment;
import com.example.EZplanner.fragments.GuideFragment;
import com.example.EZplanner.fragments.HabitFragment;
import com.example.EZplanner.fragments.StudyModeFragment;
import com.example.EZplanner.fragments.TimerHistoryFragment;
import com.example.EZplanner.fragments.TodoFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import EZplanner.R;

public class HomeScreenActivity extends AppCompatActivity {

    private int mSelectedNavItem;
    private static final String CURRENT_SELECTED_FRAGMENT = "current fragment";

    public static String username;

    public static boolean isHabitMainShown = false;
    public static boolean isHabitAddShown = false;
    public static boolean isHabitRankingShown = false;
    public static boolean isTimerHistoryShown = false;
    public static boolean isTimerMainShow = false;

    public static final String TAG = "MainActivity";

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;

    public void navigateToEdit (Fragment f) {
        fragmentManager.beginTransaction().replace(R.id.flContainer, f).commit();

    }

    public void navigateToRanking (Fragment f) {
        fragmentManager.beginTransaction().replace(R.id.flContainer, f).commit();

    }

    public void navigateBack (Fragment f) {
        fragmentManager.beginTransaction().replace(R.id.flContainer, f).commit();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        username = getIntent().getStringExtra("current_user");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                Fragment fragment;
                switch (menuItem.getItemId()) {
                    case R.id.action_study:
                        mSelectedNavItem = 1;
                        Toast.makeText(HomeScreenActivity.this, "Study!", Toast.LENGTH_SHORT).show();
                        fragment = new StudyModeFragment();
                        break;
                    case R.id.action_todo:
                        mSelectedNavItem = 2;
                        Toast.makeText(HomeScreenActivity.this, "Todo!", Toast.LENGTH_SHORT).show();
                        fragment = new TodoFragment();
                        break;
                    case R.id.action_habit:
                        mSelectedNavItem = 3;
                        fragment = new HabitFragment();
                        Toast.makeText(HomeScreenActivity.this, "Habit!", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.action_guide:
                        mSelectedNavItem = 4;
                        fragment = new GuideFragment();
                        Toast.makeText(HomeScreenActivity.this, "Guide!", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        fragment = new StudyModeFragment();
                        break;
                }
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });

        if (savedInstanceState != null) {
            mSelectedNavItem = savedInstanceState.getInt(CURRENT_SELECTED_FRAGMENT);
            if (mSelectedNavItem == 1) {
                if (isTimerHistoryShown) {
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new TimerHistoryFragment()).commit();
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.action_study);
                }
            } else if (mSelectedNavItem == 2) {
                bottomNavigationView.setSelectedItemId(R.id.action_todo);
            } else if (mSelectedNavItem == 4) {
                bottomNavigationView.setSelectedItemId(R.id.action_guide);
            } else {
                if (isHabitRankingShown) {
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new HabitRankingFragment()).commit();
                } else if (isHabitAddShown) {
                    fragmentManager.beginTransaction().replace(R.id.flContainer, new HabitEditFragment()).commit();
                } else {
                    bottomNavigationView.setSelectedItemId(R.id.action_habit);
                }
            }
        } else {
            bottomNavigationView.setSelectedItemId(R.id.action_guide);
        }
        bottomNavigationView.setItemIconTintList(null);
    }

    @Override
    public void onBackPressed() {

        if (mSelectedNavItem == 3) {
            if (isHabitAddShown) {
                Fragment fragment = new HabitFragment();
                isHabitMainShown = true;
                isHabitAddShown = false;
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                System.out.println("After exit add, the HabitMain is: " + isHabitMainShown);
                System.out.println("After exit add, the HabitAdd is: " + isHabitAddShown);
            }
            else if (isHabitRankingShown) {
                Fragment fragment = new HabitFragment();
                isHabitMainShown = true;
                isHabitRankingShown = false;
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                System.out.println("After exit ranking, the HabitMain is: " + isHabitMainShown);
                System.out.println("After exit ranking, the HabitRanking is: " + isHabitRankingShown);
            }
            else {
                super.onBackPressed();
            }
        } else if (mSelectedNavItem == 1) {
            if (isTimerHistoryShown) {
                Fragment fragment = new StudyModeFragment();
                isTimerMainShow = true;
                isTimerHistoryShown = false;
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
            }
            else {
                super.onBackPressed();
            }
        }


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CURRENT_SELECTED_FRAGMENT, mSelectedNavItem);
    }
}
