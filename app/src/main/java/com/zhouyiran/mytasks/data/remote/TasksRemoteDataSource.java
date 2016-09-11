package com.zhouyiran.mytasks.data.remote;

import android.os.Handler;
import android.support.annotation.NonNull;

import com.google.common.collect.Lists;
import com.zhouyiran.mytasks.data.Task;
import com.zhouyiran.mytasks.data.TasksDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
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

    public static TasksRemoteDataSource getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new TasksRemoteDataSource();
        }
        return INSTANCE;
    }

    private void addTask(String title, String description) {
        Task newTask = new Task(title, description);
        SERVICE_TASK_DATA.put(newTask.getId(), newTask);
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTasksLoaded(Lists.newArrayList(SERVICE_TASK_DATA.values()));
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void getTask(@NonNull String taskId, @NonNull final GetTaskCallback callback) {
        final Task task = SERVICE_TASK_DATA.get(taskId);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onTaskLoaded(task);
            }
        }, SERVICE_LATENCY_IN_MILLIS);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        SERVICE_TASK_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completeTask = new Task(task.getTitle(), task.getDescription(),
                task.getId(), true);
        SERVICE_TASK_DATA.put(task.getId(), completeTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {

    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        SERVICE_TASK_DATA.put(activeTask.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {

    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = SERVICE_TASK_DATA.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if(entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        SERVICE_TASK_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        SERVICE_TASK_DATA.remove(taskId);
    }
}
