package com.example.diary_my.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.diary_my.R;


public class Dialog_sort_notes extends DialogFragment {

    public interface NoticeDialogListener {
        public void onDialogSortNotesAlphabetClick(DialogFragment dialog);
        public void onDialogSortNotesEditDataClick(DialogFragment dialog);
        public void onDialogSortNotesCreateDateClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_sort_notes_title)
                .setItems(R.array.item_sort_notes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                mListener.onDialogSortNotesAlphabetClick(Dialog_sort_notes.this);
                                break;
                            case 1:
                                mListener.onDialogSortNotesEditDataClick(Dialog_sort_notes.this);
                                break;
                            case 2:
                                mListener.onDialogSortNotesCreateDateClick(Dialog_sort_notes.this);
                                break;
                        }
                    }
                });
        return builder.create();
    }

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
}
