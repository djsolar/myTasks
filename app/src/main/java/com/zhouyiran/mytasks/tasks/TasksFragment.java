package com.zhouyiran.mytasks.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.zhouyiran.mytasks.R;
import com.zhouyiran.mytasks.data.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouyiran on 2016/8/30.
 */
public class TasksFragment extends Fragment implements TasksContract.View {

    private TasksContract.Presenter mPresenter;

    private TaskAdapter mListAdapter;

    private View mNoTaskView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    private LinearLayout mTasksView;

    private TextView mFilteringView;

    public TasksFragment() {}

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TaskAdapter(new ArrayList<Task>(0), taskItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.tasks_frag, container, false);
        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringView = (TextView) root.findViewById(R.id.filteringLabel);
        mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);
        mNoTaskView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = (ImageView) root.findViewById(R.id.noTasksIcon);
        mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddTask();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.addNewTask();
            }
        });

        final ScrollChildSwipeRefreshLayout refreshLayout = (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refreshLayout);
        refreshLayout.setColorSchemeColors(ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
        refreshLayout.setScrollChildUp(listView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadTasks(false);
            }
        });

        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.task_frag_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;

            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;

            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showAddTask() {

    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popupMenu = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popupMenu.getMenuInflater().inflate(R.menu.filter_task, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_active:
                        mPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                        break;

                    case R.id.nav_complete:
                        mPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                        break;

                    default:
                        mPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                        break;
                }
                mPresenter.loadTasks(false);
                return true;
            }
        });
        popupMenu.show();
    }

    @Override
    public void setLoadingIndicator(final boolean active) {
        if(getView() == null)
            return;

        final ScrollChildSwipeRefreshLayout scf = (ScrollChildSwipeRefreshLayout) getView().findViewById(R.id.refreshLayout);
        scf.post(new Runnable() {
            @Override
            public void run() {
                scf.setRefreshing(active);
            }
        });
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTaskView.setVisibility(View.GONE);
    }

    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(getResources().getString(R.string.no_tasks_active), R.drawable.ic_check_circle_24dp, false);
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(getResources().getString(R.string.no_tasks_all) , R.drawable.ic_assignment_turned_in_24dp, false);
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(getResources().getString(R.string.no_tasks_completed), R.drawable.ic_verified_user_24dp, false);
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    @Override
    public void showActiveFilterLabel() {
        showMessage(getString(R.string.label_active));
    }

    @Override
    public void showCompletedFilterLabel() {
        showMessage(getString(R.string.label_completed));
    }

    @Override
    public void showAllFilterLabel() {
        showMessage(getString(R.string.label_all));
    }

    @Override
    public void showLoadingTasksError() {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }


    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTaskView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    private TaskItemListener taskItemListener = new TaskItemListener() {
        @Override
        public void onTaskClick(Task clickedTask) {
            mPresenter.openTaskDetail(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mPresenter.activateTask(activatedTask);
        }
    };


    private static class TaskAdapter extends BaseAdapter {

        private List<Task> tasks;

        private TaskItemListener listener;

        public TaskAdapter(List<Task> tasks, TaskItemListener listener) {
            setList(tasks);
            this.listener = listener;
        }

        public void replaceData(List<Task> tasks) {
            setList(tasks);
            this.notifyDataSetChanged();
        }

        private void setList(List<Task> tasks) {
            this.tasks = tasks;
        }

        @Override
        public int getCount() {
            return this.tasks.size();
        }

        @Override
        public Task getItem(int position) {
            return this.tasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rootView = convertView;
            if(rootView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rootView = inflater.inflate(R.layout.task_item, parent, false);
            }
            final Task task = getItem(position);
            TextView titleView = (TextView) rootView.findViewById(R.id.title);
            titleView.setText(task.getTitleForList());

            CheckBox cb = (CheckBox) rootView.findViewById(R.id.complete);
            cb.setChecked(task.isCompleted());
            if(task.isCompleted()) {
                cb.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            } else {
                cb.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.touch_feedback));
            }

            cb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!task.isCompleted()) {
                        listener.onCompleteTaskClick(task);
                    } else {
                        listener.onActivateTaskClick(task);
                    }
                }
            });

            titleView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTaskClick(task);
                }
            });

            return rootView;
        }
    }

    public interface TaskItemListener {

        void onTaskClick(Task clickedTask);

        void onCompleteTaskClick(Task completedTask);

        void onActivateTaskClick(Task activatedTask);
    }
}
