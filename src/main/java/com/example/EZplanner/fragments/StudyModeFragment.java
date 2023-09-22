package com.example.EZplanner.fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.EZplanner.HomeScreenActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import EZplanner.R;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class StudyModeFragment extends Fragment implements LocationConfirmFragment.LocationListener {
    private EditText mEditTextInput;
    private TextView mTextViewCountDown;
    private Button mButtonSet;
    private Button mButtonStartPause;
    private Button mButtonReset;
    private Button mButtonStop;
    private Button mButtonHistory;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;
    private long mEndTime;

    public static final String INPUT_METHOD_SERVICE = Context.INPUT_METHOD_SERVICE;
    public static final int MODE_PRIVATE = Context.MODE_PRIVATE;

    private DatabaseReference databaseReference;
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private FusedLocationProviderClient client;
    private Geocoder geocoder;
    private String latitude;
    private String longitude;
    private String address;
    private Timestamp timestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_studymode_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        geocoder = new Geocoder(getActivity());
        mEditTextInput = view.findViewById(R.id.edit_text_input);
        mTextViewCountDown = view.findViewById(R.id.text_view_countdown);
        mButtonSet = view.findViewById(R.id.button_set);
        mButtonStartPause = view.findViewById(R.id.button_start_pause);
        mButtonReset = view.findViewById(R.id.button_reset);
        mButtonStop = view.findViewById(R.id.button_stop);
        mButtonHistory = view.findViewById(R.id.button_history);

        client = LocationServices.getFusedLocationProviderClient(getActivity());

        mButtonSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = mEditTextInput.getText().toString();
                if (input.length() == 0) {
                    Toast.makeText(getContext(), "Field can't be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                long millisInput = Long.parseLong(input) * 60000;
                if (millisInput == 0) {
                    Toast.makeText(getContext(), "Please enter a positive number", Toast.LENGTH_SHORT).show();
                    return;
                }
                setTime(millisInput);
                mEditTextInput.setText("");
            }
        });

        mButtonStartPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTimerRunning) {
                    pauseTimer();
                } else {
                    startTimer();
                }
            }
        });

        mButtonReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
            }
        });

        mButtonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!mTimerRunning) {
                    Toast.makeText(getActivity(), "No timer is running!", Toast.LENGTH_SHORT).show();
                    return;
                }

                timestamp = new Timestamp(System.currentTimeMillis());
                databaseReference.child("History").child(sdf.format(timestamp))
                        .child("timer_time").setValue(millisToStringFormat(mStartTimeInMillis - mTimeLeftInMillis));
                databaseReference.child("History").child(sdf.format(timestamp))
                        .child("username").setValue(HomeScreenActivity.username);
                databaseReference.child("History").child(sdf.format(timestamp))
                        .child("timestamp").setValue(sdf.format(timestamp));

                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getCurrentLocation();
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                }

                mCountDownTimer.cancel();
                mTimerRunning = false;
                resetTimer();
            }
        });


        mButtonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeScreenActivity.isTimerHistoryShown = true;
                HomeScreenActivity.isTimerMainShow = false;

                Fragment f = new TimerHistoryFragment();
                ((HomeScreenActivity) getActivity()).navigateToEdit(f);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && (grantResults.length > 0) &&
                (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
            getCurrentLocation();
        } else {
            DialogFragment newDialog = new LocationConfirmFragment();
            newDialog.show(getChildFragmentManager(), "locationConfirmDialog");
            Toast.makeText(getActivity(), "No permission!", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {

            client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {
                        latitude = String.valueOf(location.getLatitude());
                        longitude = String.valueOf(location.getLatitude());
                        try {
                            address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1).get(0).getAddressLine(0);
                        } catch (IOException e) {
                            address = "No location";
                        }
                    } else {
                        LocationRequest locationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10000)
                                .setFastestInterval(5000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(@NonNull LocationResult locationResult) {
                                Location location1 = locationResult.getLastLocation();
                                latitude = String.valueOf(location1.getLatitude());
                                try {
                                    address = geocoder.getFromLocation(location1.getLatitude(), location1.getLongitude(), 1).get(0).getAddressLine(0);
                                } catch (IOException e) {
                                    address = "No location";
                                }
                            }
                        };
                        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                    }

                    DialogFragment newDialog = new LocationConfirmFragment();
                    newDialog.show(getChildFragmentManager(), "locationConfirmDialog");
                }
            });
        } else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }


    }

    private void setTime(long milliseconds) {
        mStartTimeInMillis = milliseconds;
        resetTimer();
        closeKeyboard();
    }
    private void startTimer() {
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }
            @Override
            public void onFinish() {
                mTimerRunning = false;
                updateWatchInterface();
            }
        }.start();
        mTimerRunning = true;
        updateWatchInterface();
    }
    private void pauseTimer() {
        mCountDownTimer.cancel();
        mTimerRunning = false;
        updateWatchInterface();
    }
    private void resetTimer() {
        mTimeLeftInMillis = mStartTimeInMillis;
        updateCountDownText();
        updateWatchInterface();
    }
    private void updateCountDownText() {
        mTextViewCountDown.setText(millisToStringFormat(mTimeLeftInMillis));
    }

    private String millisToStringFormat(long millis) {
        int hours = (int) (millis / 1000) / 3600;
        int minutes = (int) ((millis / 1000) % 3600) / 60;
        int seconds = (int) (millis / 1000) % 60;
        String timeLeftFormatted;
        if (hours > 0) {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%d:%02d:%02d", hours, minutes, seconds);
        } else {
            timeLeftFormatted = String.format(Locale.getDefault(),
                    "%02d:%02d", minutes, seconds);
        }
        return timeLeftFormatted;
    }

    private void updateWatchInterface() {
        if (mTimerRunning) {
            mEditTextInput.setVisibility(View.INVISIBLE);
            mButtonSet.setVisibility(View.INVISIBLE);
            mButtonReset.setVisibility(View.INVISIBLE);
            mButtonStartPause.setText("Pause");
        } else {
            mEditTextInput.setVisibility(View.VISIBLE);
            mButtonSet.setVisibility(View.VISIBLE);
            mButtonStartPause.setText("Start");
            if (mTimeLeftInMillis < 1000) {
                mButtonStartPause.setVisibility(View.INVISIBLE);
            } else {
                mButtonStartPause.setVisibility(View.VISIBLE);
            }
            if (mTimeLeftInMillis < mStartTimeInMillis) {
                mButtonReset.setVisibility(View.VISIBLE);
            } else {
                mButtonReset.setVisibility(View.INVISIBLE);
            }
        }
    }
    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("startTimeInMillis", mStartTimeInMillis);
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        SharedPreferences prefs = getActivity().getSharedPreferences("prefs", MODE_PRIVATE);
        mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
        mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        updateWatchInterface();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            if (mTimeLeftInMillis < 0) {
                mTimeLeftInMillis = 0;
                mTimerRunning = false;
                updateCountDownText();
                updateWatchInterface();
            } else {
                startTimer();
            }
        }
    }

    @Override
    public void onDialogPositiveClickLocationConfirmDialog(DialogFragment dialog) {
        DialogFragment locationInputFragment = new LocationInputFragment(this.address);
        locationInputFragment.show(getChildFragmentManager(), "locationInputDialog");
    }

    @Override
    public void onDialogNegativeClickLocationConfirmDialog(DialogFragment dialog) {
        databaseReference.child("History").child(sdf.format(timestamp))
                .child("location").setValue("No Location");
    }

    @Override
    public void onDialogPositiveClickLocationInputDialog(String geoLocation, String inputLocation) {
        String location;
        if (inputLocation == null || inputLocation.equals("") || inputLocation.isEmpty()) {
            if (geoLocation == null) {
                location = "Default Location";
            } else {
                location = geoLocation;
            }
        } else {
            location = inputLocation;
        }
        databaseReference.child("History").child(sdf.format(timestamp))
                .child("location").setValue(location);
    }

    @Override
    public void onDialogNegativeClickLocationInputDialog(DialogFragment dialog) {
    }
}