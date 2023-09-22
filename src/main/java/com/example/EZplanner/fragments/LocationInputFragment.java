package com.example.EZplanner.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import EZplanner.R;

public class LocationInputFragment extends DialogFragment {

    LocationConfirmFragment.LocationListener locationInputListener;

    private String geoCoderLocation;

    private EditText locationInput;

    public LocationInputFragment(String geoCoderLocation) {
        this.geoCoderLocation = geoCoderLocation;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            locationInputListener = (LocationConfirmFragment.LocationListener) getParentFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Must implement LocationConfirmListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragmet_dialog_location_input, null);
        locationInput = view.findViewById(R.id.locationInput);
        builder.setView(view);
        builder.setMessage(this.geoCoderLocation)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        locationInputListener.onDialogPositiveClickLocationInputDialog(geoCoderLocation, locationInput.getText().toString());
                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // User cancelled the dialog
//                    locationInputListener.onDialogNegativeClickLocationInputDialog(LocationInputFragment.this);
//                    }
//                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
