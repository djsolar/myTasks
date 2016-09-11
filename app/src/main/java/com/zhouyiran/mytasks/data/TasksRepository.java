package com.zhouyiran.mytasks.data;

import android.support.annotation.NonNull;

import com.zhouyiran.mytasks.data.local.TasksLocalDataSource;
import com.zhouyiran.mytasks.data.remote.TasksRemoteDataSource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zhouyiran on 16/9/11.
 */
public class TasksRepository implements TasksDataSource {

    private static TasksRepository INSTANCE = null;

    private final TasksLocalDataSource mTasksLocalDataSource;

    private final TasksRemoteDataSource mTasksRemoteDataSource;

    Map<String, Task> mCacheTasks;

    boolean mCacheIsDirty = false;

    private TasksRepository(TasksLocalDataSource tasksLocalDataSource,
                            TasksRemoteDataSource tasksRemoteDataSource) {
        this.mTasksLocalDataSource = tasksLocalDataSource;
        this.mTasksRemoteDataSource = tasksRemoteDataSource;
    }

    public static TasksRepository getInstance(@NonNull TasksLocalDataSource tasksLocalDataSource,
                                              @NonNull TasksRemoteDataSource tasksRemoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new TasksRepository(tasksLocalDataSource, tasksRemoteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getTasks(@NonNull final LoadTasksCallback callback) {
        if(mCacheTasks != null && !mCacheIsDirty) {
            callback.onTasksLoaded(new ArrayList<>(mCacheTasks.values()));
            return;
        }

        if(mCacheIsDirty) {
            getTasksFromRemoteDataSource(callback);
        } else {
            mTasksLocalDataSource.getTasks(new LoadTasksCallback() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    callback.onTasksLoaded(tasks);
                }

                @Override
                public void onDataNotAvailable() {
                    getTasksFromRemoteDataSource(callback);
                }
            });
        }
    }

    private void getTasksFromRemoteDataSource(@NonNull final LoadTasksCallback callback) {
        mTasksRemoteDataSource.getTasks(new LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                refreshCache(tasks);
                refreshLocalDataSource(tasks);
                callback.onTasksLoaded(new ArrayList<>(mCacheTasks.values()));
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    private void refreshLocalDataSource(List<Task> tasks) {
        mTasksLocalDataSource.deleteAllTasks();
        for(Task task : tasks) {
            mTasksLocalDataSource.saveTask(task);
        }
    }

    private void refreshCache(List<Task> tasks) {
        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        mCacheTasks.clear();
        for(Task task : tasks) {
            mCacheTasks.put(task.getId(), task);
        }
        mCacheIsDirty = false;
    }

    @Override
    public void getTask(@NonNull final String taskId, @NonNull final GetTaskCallback callback) {
        Task cacheTask = getTaskWithId(taskId);
        if(cacheTask != null) {
            callback.onTaskLoaded(cacheTask);
            return;
        }

        mTasksLocalDataSource.getTask(taskId, new GetTaskCallback() {
            @Override
            public void onTaskLoaded(Task task) {
                callback.onTaskLoaded(task);
            }

            @Override
            public void onDataNotAvailable() {
                mTasksRemoteDataSource.getTask(taskId, new GetTaskCallback() {
                    @Override
                    public void onTaskLoaded(Task task) {
                        callback.onTaskLoaded(task);
                    }

                    @Override
                    public void onDataNotAvailable() {
                        callback.onDataNotAvailable();
                    }
                });
            }
        });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        mTasksRemoteDataSource.saveTask(task);
        mTasksLocalDataSource.saveTask(task);

        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        mCacheTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);

        Task completeTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        mCacheTasks.put(completeTask.getId(), completeTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        completeTask(getTaskWithId(taskId));
    }

    private Task getTaskWithId(String taskId) {
        if(mCacheTasks == null || mCacheTasks.isEmpty()) {
            return null;
        }
        return mCacheTasks.get(taskId);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);

        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        mCacheTasks.put(activeTask.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        activateTask(getTaskWithId(taskId));
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();

        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCacheTasks.entrySet().iterator();
        while(it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if(entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();

        if(mCacheTasks == null) {
            mCacheTasks = new LinkedHashMap<>();
        }
        mCacheTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(taskId);
        mTasksLocalDataSource.deleteTask(taskId);
        mCacheTasks.remove(taskId);
    }
}
