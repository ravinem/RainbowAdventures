package com.example.mapwithmarker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.location.Address;
import android.os.Bundle;

import java.util.ArrayList;

/**
 * Created by ravis on 08-01-2018.
 */

public class AddressListDialogFragement extends DialogFragment {

    public static int index = 0;
    public interface NoticeDialogListener {
        public void onDialogItemClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        CharSequence[] addrs =     getArguments().getCharSequenceArray("locations");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_select_place_value)
                .setItems(addrs, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        AddressListDialogFragement.index = which;
                        mListener.onDialogItemClick(AddressListDialogFragement.this);
                    }
                });
        return builder.create();
    }
}
