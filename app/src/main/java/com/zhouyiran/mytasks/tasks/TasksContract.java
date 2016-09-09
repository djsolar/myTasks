package com.zhouyiran.mytasks.tasks;

import android.support.annotation.NonNull;

import com.zhouyiran.mytasks.BasePresenter;
import com.zhouyiran.mytasks.BaseView;
import com.zhouyiran.mytasks.data.Task;

import java.util.List;

/**
 * Created by zhouyiran on 2016/8/30.
 */
public interface TasksContract {

    interface View extends BaseView<Presenter> {

        void showAddTask();

        void showFilteringPopUpMenu();

        void setLoadingIndicator(final boolean active);

        void showTasks(List<Task> tasks);

        void showNoActiveTasks();

        void showNoTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showLoadingTasksError();
    }


    interface Presenter extends BasePresenter {

        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetail(@NonNull Task requestedTask);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType filterType);

        void openTaskDetails(Task task);

        void completeTask(Task task);

        void activateTask(Task task);
    }
}
