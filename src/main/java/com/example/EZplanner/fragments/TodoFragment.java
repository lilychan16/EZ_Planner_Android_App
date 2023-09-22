package com.example.EZplanner.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import EZplanner.R;

import com.example.EZplanner.HomeScreenActivity;
import com.example.EZplanner.edit.TodoItemsAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TodoFragment extends Fragment {
    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";

    public static final String INPUT_METHOD_SERVICE = Context.INPUT_METHOD_SERVICE;


    List<String> items;

    Button btnAdd;
    EditText etItem;
    RecyclerView rvItem;
    TodoItemsAdapter todoItemsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_main, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnAdd = view.findViewById(R.id.btnAdd);
        etItem = view.findViewById(R.id.etItem);
        rvItem = view.findViewById(R.id.rvItem);

        loadItems();

        /*
        TodoItemsAdapter.OnLongClickListener onItemLongClicked = new TodoItemsAdapter.OnLongClickListener() {
            public void onItemLongClicked(int position) {
                items.remove(position);
                todoItemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Items was removed", Toast.LENGTH_SHORT).show();
                saveItems();

            }
        };
         */


        TodoItemsAdapter.OnDoneButtonClickListener onDoneButtonClickedListener = new TodoItemsAdapter.OnDoneButtonClickListener() {
            @Override
            public void onDoneButtonClicked(int position) {
                items.remove(position);
                todoItemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getContext(), "Items was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };


        todoItemsAdapter = new TodoItemsAdapter(items, onDoneButtonClickedListener);
        rvItem.setAdapter(todoItemsAdapter);
        rvItem.setLayoutManager(new LinearLayoutManager(getContext()));

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String todoItem = etItem.getText().toString();
                if (todoItem.trim().length() == 0) {
                    Toast.makeText(getContext(), "Cannot add blank item", Toast.LENGTH_SHORT).show();
                }
                else {
                    items.add(todoItem);
                    Toast.makeText(getContext(), "Item was added", Toast.LENGTH_SHORT).show();
                    todoItemsAdapter.notifyItemInserted(items.size()-1);
                }
                etItem.setText("");
                saveItems();
                closeKeyboard();
            }
        });
    }

    private File getDataFile () {
        return new File(getContext().getFilesDir(), HomeScreenActivity.username+"_todo.txt");
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
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
            if (items == null) {
                Log.i("todo loadItems()", "null");
            } else if (items.size() == 0){
                Log.i("todo loadItems()", "empty");
            } else {
                Log.i("todo loadItems()", items.toString());
            }
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }

    private void saveItems(){
        if (items == null) {
            Log.i("TodoFragmentLoadItems()", "null");
        } else {
            Log.i("TodoFragmentloadItems()", items.toString());
        }

        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("HabitFrag", "Error writing items", e);
        }
    }
}