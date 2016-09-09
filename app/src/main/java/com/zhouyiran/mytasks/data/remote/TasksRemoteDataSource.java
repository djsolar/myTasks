package com.zhouyiran.mytasks.data.remote;

import android.support.annotation.NonNull;

import com.zhouyiran.mytasks.data.Task;
import com.zhouyiran.mytasks.data.TasksDataSource;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhouyiran on 2016/9/8.
 */
public class TasksRemoteDataSource implements TasksDataSource {

    private static TasksRemoteDataSource INSTANCE;

    private final static int SERVICE_LATENCY_IN_MILLIS = 5000;

    private final static Map<String, Task> SERVICE_TASK_DATA;

    static {
        SERVICE_TASK_DATA = new HashMap<>(2);
    }

    @Override
    public void getTasks(@NonNull LoadTasksCallback callback) {

    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull GetTaskCallback callback) {

    }

    @Override
    public void saveTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull Task task) {

    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Task task) {

    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {

    }

    @Override
    public void deleteTask(@NonNull String taskId) {

    }
}
