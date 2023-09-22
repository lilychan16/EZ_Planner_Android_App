package com.example.EZplanner.edit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.EZplanner.fragments.TodoFragment;

import EZplanner.R;

public class TodoEditFragment extends Fragment {

    EditText etItem;
    Button btnSave;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_todo_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etItem = view.findViewById(R.id.etItem);
        btnSave = view.findViewById(R.id.btnSave);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Edit item");
        etItem.setText(getActivity().getIntent().getStringExtra(TodoFragment.KEY_ITEM_TEXT));
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra(TodoFragment.KEY_ITEM_TEXT, etItem.getText().toString());
                intent.putExtra(TodoFragment.KEY_ITEM_POSITION, getActivity().getIntent().getExtras().getInt(TodoFragment.KEY_ITEM_POSITION));

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
    }
}