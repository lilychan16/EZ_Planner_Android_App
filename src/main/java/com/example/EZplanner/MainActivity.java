package com.example.EZplanner;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import EZplanner.R;

public class MainActivity extends AppCompatActivity {
    private static String CLIENT_REGISTRATION_TOKEN;

    private DatabaseReference databaseReference;
    private EditText username;
    private Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        username = (EditText) findViewById(R.id.et_username);
        login = (Button) findViewById(R.id.btn_login);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (username.getText().toString().matches("")) {
                    Toast.makeText(MainActivity.this, "Please enter a username", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    databaseReference.child("Users").child(username.getText().toString())
                            .child("username").setValue(username.getText().toString());

                    // Generate the token for the first time, then no need to do later
                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Something is wrong!", Toast.LENGTH_SHORT).show();
                            } else {
                                if (CLIENT_REGISTRATION_TOKEN == null) {
                                    CLIENT_REGISTRATION_TOKEN = task.getResult();
                                }
                                Log.e("CLIENT_REGISTRATION_TOKEN", CLIENT_REGISTRATION_TOKEN);
                                Toast.makeText(MainActivity.this, "Login succeed", Toast.LENGTH_SHORT).show();
                                databaseReference.child("Users").child(username.getText().toString())
                                        .child("token").setValue(CLIENT_REGISTRATION_TOKEN);
                            }
                        }
                    });

                    openMainActivity();
                }


            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(MainActivity.this, HomeScreenActivity.class);
        String name = username.getText().toString();
        intent.putExtra("current_user", name);
        startActivity(intent);
    }
}
