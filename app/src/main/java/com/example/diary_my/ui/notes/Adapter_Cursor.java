package com.example.diary_my.ui.notes;


import android.database.Cursor;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.diary_my.db.Contact_Database;

public abstract class Adapter_Cursor<ViewHolder extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<ViewHolder> {

    protected Cursor cursor; // Курсор
    protected boolean isDataValid; // Валидны ли данные
    protected int idColumnIndex; // Индекс столбца ID в курсоре

    public Adapter_Cursor(Cursor cursor) {
        super();

        this.cursor = cursor;

        // Данные корректны если курсор не null
        isDataValid = cursor != null;

        // Пытаемся получить индекс столбца ID, если курсор не null, в ином случае -1
        idColumnIndex = cursor != null
                ? cursor.getColumnIndexOrThrow(Contact_Database.Notes._ID)
                : -1;

        // Каждый элемент имеет уникальный ID
        setHasStableIds(true);
    }

    public abstract void onBindViewHolder(ViewHolder viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        // Если данные некорректны — кидаем исключение
        if (!isDataValid) {
            throw new IllegalStateException("Cursor is not valid!");
        }

        // Попробовали перейти к определённой строке, но это не получилось
        if (!cursor.moveToPosition(position)) {
            throw new IllegalStateException("Can not move to position " + position);
        }

        // Вызываем новый метод
        onBindViewHolder(viewHolder, cursor);
    }

    @Override
    public int getItemCount() {
        if (isDataValid && cursor != null) {
            return cursor.getCount();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {

        // Если с данными всё хорошо и есть курсор
        if (isDataValid && cursor != null) {

            // Если смогли найти нужную строку в курсоре
            if (cursor.moveToPosition(position)) {

                // Возвращаем значение столбца ID
                return cursor.getLong(idColumnIndex);
            }
        }

        // Во всех остальных случаях возвращаем дефолтное значение
        return RecyclerView.NO_ID;
    }

    @Nullable
    public Cursor swapCursor(Cursor newCursor) {
        // Если курсор не изменился — ничего не заменяем
        if (newCursor == this.cursor) {
            return null;
        }

        Cursor oldCursor = this.cursor;
        this.cursor = newCursor;

        if (newCursor != null) {
            idColumnIndex = newCursor.getColumnIndexOrThrow(Contact_Database.Notes._ID);
            isDataValid = true;
            notifyDataSetChanged();
        } else {
            idColumnIndex = -1;
            isDataValid = false;
            // Сообщаем, что данных в адаптере больше нет
            notifyItemRangeRemoved(0, getItemCount());
        }
        return oldCursor;

    }
}