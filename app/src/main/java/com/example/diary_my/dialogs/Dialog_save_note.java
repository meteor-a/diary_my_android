package com.example.diary_my.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.diary_my.R;


public class Dialog_save_note extends DialogFragment implements DialogInterface.OnClickListener {

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private Dialog_save_note.NoticeDialogListener mListener;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder adb = new AlertDialog.Builder(getActivity())
                .setTitle(R.string.dialog_save_note_title)
                .setPositiveButton(R.string.dialog_save_note_yes, this)
                .setNegativeButton(R.string.dialog_save_note_no, this)
                .setMessage(R.string.dialog_save_note_verification);
        return adb.create();
    }

    @Override
    public void onAttach(@NonNull Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (Dialog_save_note.NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public void onClick(DialogInterface dialog, int which) {
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                mListener.onDialogPositiveClick(Dialog_save_note.this);
                break;
            case Dialog.BUTTON_NEGATIVE:
                mListener.onDialogNegativeClick(Dialog_save_note.this);
                break;
        }
    }

}
