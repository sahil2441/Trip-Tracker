package me.sahiljain.locationstat.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import me.sahiljain.locationstat.R;

/**
 * Created by sahil on 17/2/15.
 */
public class UseCurrentLocationDialog extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.use_current_location_dialog)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogPositiveClick(UseCurrentLocationDialog.this);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialogListener.onDialogNegativeClick(UseCurrentLocationDialog.this);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */

    public interface UseCurrentLocationDialogListener {
        public void onDialogPositiveClick(DialogFragment dialogFragment);

        public void onDialogNegativeClick(DialogFragment dialogFragment);
    }

    // Use this instance of the interface to deliver action events
    UseCurrentLocationDialogListener dialogListener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (UseCurrentLocationDialogListener) activity;


        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString() +
                    "The Activity must implement the UseCurrentLocationDialogListener");


        }
    }
}
