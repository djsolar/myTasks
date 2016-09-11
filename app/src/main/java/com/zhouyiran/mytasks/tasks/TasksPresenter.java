package com.zhouyiran.mytasks.tasks;

import android.support.annotation.NonNull;

import com.zhouyiran.mytasks.data.Task;
import com.zhouyiran.mytasks.data.TasksDataSource;
import com.zhouyiran.mytasks.data.TasksRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyiran on 16/9/11.
 */
public class TasksPresenter implements TasksContract.Presenter {

    private final TasksRepository mTasksRepository;

    private final TasksContract.View mTasksView;

    private boolean mFirstLoad = true;

    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    public TasksPresenter(@NonNull TasksRepository tasksRepository, @NonNull TasksContract.View tasksView) {
        this.mTasksRepository = tasksRepository;
        this.mTasksView = tasksView;
        this.mTasksView.setPresenter(this);
    }

    @Override
    public void result(int requestCode, int resultCode) {

    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
    }

    private void loadTasks(boolean forceUpdate, final boolean showLoadingUi) {
        if(showLoadingUi) {
            mTasksView.setLoadingIndicator(true);
        }
        if(forceUpdate) {
            mTasksRepository.refreshTasks();
        }

        mTasksRepository.getTasks(new TasksDataSource.LoadTasksCallback() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                List<Task> showTasks = new ArrayList<Task>();

                for(Task task : tasks) {

                    switch (mCurrentFiltering) {
                        case ALL_TASKS:
                            showTasks.add(task);
                            break;

                        case ACTIVE_TASKS:
                            if(task.isActive()) {
                                showTasks.add(task);
                            }
                            break;

                        case COMPLETED_TASKS:
                            if(task.isCompleted()) {
                                showTasks.add(task);
                            }
                            break;

                        default:
                            showTasks.add(task);
                    }
                }
                if (!mTasksView.isActive()) return;

                if(showLoadingUi) {
                    mTasksView.setLoadingIndicator(false);
                }
                processTasks(showTasks);
            }

            @Override
            public void onDataNotAvailable() {
                if(!mTasksView.isActive()) return;

                mTasksView.showLoadingTasksError();
            }
        });
    }

    private void processTasks(List<Task> showTasks) {
        if(showTasks.isEmpty()) {
            processEmptyTasks();
        } else {
            mTasksView.showTasks(showTasks);
            showFilterLabel();
        }

    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ALL_TASKS:
                mTasksView.showAllFilterLabel();
                break;

            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;

            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;

            default:
                mTasksView.showAllFilterLabel();

        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;

            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;

            default:
                mTasksView.showNoTasks();
        }
    }

    @Override
    public void addNewTask() {

    }

    @Override
    public void openTaskDetail(@NonNull Task requestedTask) {

    }

    @Override
    public void clearCompletedTasks() {

    }

    @Override
    public void setFiltering(TasksFilterType filterType) {
        mCurrentFiltering = filterType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }

    @Override
    public void openTaskDetails(Task task) {

    }

    @Override
    public void completeTask(Task task) {

    }

    @Override
    public void activateTask(Task task) {

    }

    @Override
    public void start() {
        loadTasks(false);
    }
}
