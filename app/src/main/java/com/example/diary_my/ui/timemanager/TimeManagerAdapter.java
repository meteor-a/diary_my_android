package com.example.diary_my.ui.timemanager;

import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.diary_my.R;
import com.example.diary_my.db.Contact_Database;
import com.example.diary_my.ui.notes.Adapter_Cursor;

public class TimeManagerAdapter extends Adapter_Cursor_TimeManager<TimeManagerAdapter.ViewHolder>{

    private final onTaskClickListener onTaskClickListener;
    private final onCheckClickListener onCheckClickListener;

    public TimeManagerAdapter(Cursor cursor, onTaskClickListener onTaskClickListener, onCheckClickListener onCheckClickListener) {
        super(cursor);
        this.onTaskClickListener = onTaskClickListener;
        this.onCheckClickListener = onCheckClickListener;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, Cursor cursor) {
        int idColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Tasks._ID);
        long id = cursor.getLong(idColumnIndex);
        viewHolder.itemView.setTag(id);

        int taskColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.TASK_COLUMN);
        String title = cursor.getString(taskColumnIndex);
        viewHolder.taskText.setText(title);

        int dateColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.NOTIFICATION_DATE_COLUMN);
       // int timeColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.NOTIFICATION_TIME_COLUMN);
        String dateNotification = cursor.getString(dateColumnIndex);
       // String timeNotification = cursor.getString(timeColumnIndex);
        String notification = dateNotification;
        viewHolder.dateTv.setText(notification);

        int syncronizedColumn = cursor.getColumnIndex(Contact_Database.Tasks.SYNCHRONIZED);
        long syncTask = cursor.getInt(syncronizedColumn);
        if (syncTask == 0) {
            viewHolder.sync_indicator.setImageResource(R.drawable.sync_err);
        } else {
            viewHolder.sync_indicator.setImageResource(R.drawable.sync_ok);
        }


        int completeColumnIndex = cursor.getColumnIndexOrThrow(Contact_Database.Tasks.COMPLETE);
        long complete = cursor.getLong(completeColumnIndex);

        if (complete == 0) {
            viewHolder.checkBox.setChecked(false);
        } else {
            viewHolder.checkBox.setChecked(true);
        }

        viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckClickListener.onCheckedChanged(id);
            }
        });
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_recyclerview_timemanager, parent, false);

        return new ViewHolder(view);
    }

    /**
     * View holder
     */
    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView taskText;
        private final TextView dateTv;
        private final CheckBox checkBox;
        private final ImageView sync_indicator;


        public ViewHolder(View itemView) {
            super(itemView);
            this.taskText = itemView.findViewById(R.id.text_content_task);
            this.dateTv = itemView.findViewById(R.id.data_content);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long taskId = (Long) v.getTag();
                    onTaskClickListener.onTaskClick(taskId);
                }
            });
            this.checkBox = itemView.findViewById(R.id.check_ready_task);
            this.sync_indicator = itemView.findViewById(R.id.icon_sync);

        }
    }

    public interface onTaskClickListener {
        void onTaskClick(long taskId);
    }

    public interface onCheckClickListener {
        void onCheckedChanged(long taskId);
    }

}
