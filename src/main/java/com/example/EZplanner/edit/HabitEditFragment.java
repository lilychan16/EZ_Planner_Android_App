package com.example.EZplanner.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EZplanner.HomeScreenActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.EZplanner.MainActivity;
import EZplanner.R;

import com.example.EZplanner.components.Habit;
import com.example.EZplanner.fragments.HabitFragment;
import com.google.android.material.snackbar.Snackbar;

import org.apache.commons.io.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class HabitEditFragment extends Fragment {


    EditText habit;

    Button btnSave;

    public static final String INPUT_METHOD_SERVICE = Context.INPUT_METHOD_SERVICE;

    static final String URL = "https://my-json-server.typicode.com/octari/CS5520-db/habits/";

    private TextView textView_activity;
    private TextView textView_frequency;
    private TextView textView_timeUnit;
    private Button btn_search;
    private RequestQueue mQueue;

    List<String> habits;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_habit_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        habit = view.findViewById(R.id.habit);
        btnSave = view.findViewById(R.id.btnSave);

        textView_activity = view.findViewById(R.id.textView_activity);
        textView_frequency = view.findViewById(R.id.textView_frequency);
        textView_timeUnit = view.findViewById(R.id.textView_timeUnit);
        btn_search = view.findViewById(R.id.btn_search);

        mQueue = Volley.newRequestQueue(this.getContext());

        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jsonParse(v);
            }
        });

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Add Habit");


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String habitContent = habit.getText().toString();
                if (habitContent.trim().length() == 0) {
                    Toast.makeText(getContext(), "Cannot add blank item", Toast.LENGTH_SHORT).show();
                } else {
                    Habit newHabit = new Habit();
                    newHabit.setHabit(habitContent);
                    loadItems();
                    habits.add(newHabit.toString());
                    closeKeyboard();
                    // 1. save info to data.txt
                    saveItems();
                    Toast.makeText(getActivity(), "Habit saved successfully!", Toast.LENGTH_SHORT).show();
                    System.out.println("123" + getContext().getFilesDir());
                }
                //Habit newHabit = new Habit();
                //newHabit.setHabit(habit.getText().toString());

                //loadItems();
                //habits.add(newHabit.toString());
                //closeKeyboard();

                // 1. save info to data.txt
                //saveItems();
                //Toast.makeText(getActivity(), "Habit saved successfully!", Toast.LENGTH_SHORT).show();
                //System.out.println("123" + getContext().getFilesDir());
                // 2. navigate to previous page
                Fragment f = new HabitFragment();
                ((HomeScreenActivity) getActivity()).navigateBack(f);
            }
        });
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void loadItems() {
        try {
            habits = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            habits = new ArrayList<>();
        }
    }

    private void jsonParse(final View view) {
        String urlNew = URL + habit.getText().toString().toLowerCase();
//        Log.d("ASDF",urlNew);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, urlNew, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
//                    Toast.makeText(getActivity(), "response", Toast.LENGTH_SHORT).show();
                        response(response, view);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
//                    Toast.makeText(Network.this, "error", Toast.LENGTH_SHORT).show();
                        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                        inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
                        Snackbar.make(view, habit.getText()+": Activity not found, press SAVE to add manually", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

            }
        });
        mQueue.add(request);
    }

    private void response(JSONObject response, View view) {
        try {
            JSONArray jsonArray = response.toJSONArray(response.names());
            assert jsonArray != null;
            textView_activity.setText(jsonArray.getString(0));
            textView_frequency.setText(jsonArray.getString(1));
            textView_timeUnit.setText(jsonArray.getString(2));


            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getApplicationWindowToken(),0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void saveItems(){
        try {
            FileUtils.writeLines(getDataFile(), habits);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

    private File getDataFile () {
        return new File(getContext().getFilesDir(), HomeScreenActivity.username+"_habit.txt");
    }
}