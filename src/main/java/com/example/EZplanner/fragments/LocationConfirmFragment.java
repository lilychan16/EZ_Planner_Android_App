package com.example.EZplanner.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class LocationConfirmFragment extends DialogFragment {



    public interface LocationListener {
        void onDialogPositiveClickLocationConfirmDialog(DialogFragment dialog);
        void onDialogNegativeClickLocationConfirmDialog(DialogFragment dialog);

        void onDialogPositiveClickLocationInputDialog(String geoLocation, String inputLocation);
        void onDialogNegativeClickLocationInputDialog(DialogFragment dialog);
    }

    LocationListener locationConfirmListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            locationConfirmListener = (LocationListener) getParentFragment();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException("Must implement LocationConfirmListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        builder.setMessage("Good Job! Do you want to label your location?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // FIRE ZE MISSILES!
                        locationConfirmListener.onDialogPositiveClickLocationConfirmDialog(LocationConfirmFragment.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        locationConfirmListener.onDialogNegativeClickLocationConfirmDialog(LocationConfirmFragment.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();    }
}
