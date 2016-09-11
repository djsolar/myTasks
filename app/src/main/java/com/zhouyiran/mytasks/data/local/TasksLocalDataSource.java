package com.zhouyiran.mytasks.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.zhouyiran.mytasks.data.Task;
import com.zhouyiran.mytasks.data.TasksDataSource;
import com.zhouyiran.mytasks.data.local.TasksPersistenceContract.TaskEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyiran on 16/9/11.
 */
public class TasksLocalDataSource implements TasksDataSource {

    private static TasksLocalDataSource INSTANCE;

    private TasksDbHelper mDbHelper;

    private TasksLocalDataSource(@NonNull Context context) {
        mDbHelper = new TasksDbHelper(context);
    }
    public static TasksLocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context);
        }
        return INSTANCE;
    }


    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {
        List<Task> tasks = new ArrayList<>();
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String[] projections = new String[] {
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };
        Cursor cursor = database.query(
                TaskEntry.TABLE_NAME, projections, null, null, null, null, null);
        if(cursor != null  && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String itemId =
                        cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
                String title =
                        cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
                String description =
                        cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
                boolean completed =
                        cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
                Task task = new Task(title, description, itemId, completed);
                tasks.add(task);
            }
        }
        if(cursor != null) {
            cursor.close();
        }
        database.close();
        if(tasks.isEmpty()) {
            callback.onDataNotAvailable();
        } else {
            callback.onTasksLoaded(tasks);
        }
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        String[] projection = new String[] {
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_ENTRY_ID,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_TITLE,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_DESCRIPTION,
                TasksPersistenceContract.TaskEntry.COLUMN_NAME_COMPLETED
        };
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[] { taskId };

        Cursor cursor =
                database.query(TaskEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        Task task = null;
        if(cursor != null  && cursor.getCount() > 0) {
            cursor.moveToFirst();
            String itemId =
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
            String title =
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
            String description =
                    cursor.getString(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
            boolean completed =
                    cursor.getInt(cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
            task = new Task(title, description, itemId, completed);
        }
        if(cursor != null) {
            cursor.close();
        }
        database.close();
        if(task != null) {
            callback.onTaskLoaded(task);
        } else {
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void saveTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        contentValues.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        contentValues.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        contentValues.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());

        db.insert(TaskEntry.TABLE_NAME, null, contentValues);

        db.close();
    }

    @Override
    public void completeTask(@NonNull Task task) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[] { task.getId() };

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void completeTask(@NonNull String taskId) {


    }

    @Override
    public void activateTask(@NonNull Task task) {

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[] { task.getId() };

        db.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);

        db.close();
    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = new String[] { "1" };

        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        db.delete(TaskEntry.TABLE_NAME, null, null);

        db.close();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = new String[] { taskId };
        db.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);

        db.close();
    }
}
