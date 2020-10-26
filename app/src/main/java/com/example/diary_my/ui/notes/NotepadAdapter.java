package com.example.diary_my.ui.notes;

import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.diary_my.R;
import com.example.diary_my.db.Contact_Database;

public class NotepadAdapter extends Adapter_Cursor<NotepadAdapter.ViewHolder>{

    private final OnNoteClickListener onNoteClickListener;

    public NotepadAdapter(Cursor cursor, OnNoteClickListener onNoteClickListener) {
        super(cursor);
        this.onNoteClickListener = onNoteClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Notes._ID);
        long id = cursor.getLong(idColumnIndex);
        viewHolder.itemView.setTag(id);

        int titleColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Notes.TOPIC_COLUMN);
        String title = cursor.getString(titleColumnIndex);
        viewHolder.topicTv.setText(title);

        int dateColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Notes.UPDATED_TS_COLUMN);
        String updatedTs = cursor.getString(dateColumnIndex);
        //Date date = new Date(updatedTs);
        viewHolder.dateTv.setText(updatedTs);

        int syncColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Notes.SYNCHRONIZED);
        long synchronized_indicator = cursor.getLong(syncColumnIndex);
        if (synchronized_indicator == 0) {
            viewHolder.sync_indicator.setImageResource(R.drawable.sync_err);
        } else {
            viewHolder.sync_indicator.setImageResource(R.drawable.sync_ok);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recyclerview_notes, parent, false);

        return new ViewHolder(view);
    }

    /**
     * View holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView topicTv;
        private final TextView dateTv;
        private final ImageView sync_indicator;


        public ViewHolder(View itemView) {
            super(itemView);
            this.topicTv = itemView.findViewById(R.id.topic_tv);
            this.dateTv = itemView.findViewById(R.id.date_tv);
            this.sync_indicator = itemView.findViewById(R.id.icon_sync);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long noteId = (Long) v.getTag();

                    onNoteClickListener.onNoteClick(noteId);
                }
            });
        }
    }

    public interface OnNoteClickListener {
        void onNoteClick(long noteId);
    }

}
