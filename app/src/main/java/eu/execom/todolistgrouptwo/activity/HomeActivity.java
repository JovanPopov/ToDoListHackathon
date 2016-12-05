package eu.execom.todolistgrouptwo.activity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import java.util.List;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.adapter.TaskAdapter;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.api.errorHandler.MyErrorHandler;
import eu.execom.todolistgrouptwo.database.wrapper.TaskDAOWrapper;
import eu.execom.todolistgrouptwo.database.wrapper.UserDAOWrapper;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.model.User;
import eu.execom.todolistgrouptwo.preference.UserPreferences_;
import eu.execom.todolistgrouptwo.protocol.TaskDoneListener;
import eu.execom.todolistgrouptwo.util.NetworkingUtils;

/**
 * Home {@link AppCompatActivity Activity} for navigation and listing all tasks.
 */
@EActivity(R.layout.activity_home)
@OptionsMenu(R.menu.menu_items)
public class HomeActivity extends AppCompatActivity{

    /**
     * Used for logging purposes.
     */
    private static final String TAG = HomeActivity.class.getSimpleName();

    /**
     * Used for identifying results from different activities.
     */
    protected static final int ADD_TASK_REQUEST_CODE = 42;
    public static final int LOGIN_REQUEST_CODE = 420; // BLAZE IT
    protected static final int EDIT_TASK_REQUEST_CODE = 421;

    /**
     * Tasks are kept in this list during a user session.
     */
    private List<Task> tasks;



    /**
     * {@link FloatingActionButton FloatingActionButton} for starting the
     * {@link AddTaskActivity AddTaskActivity}.
     */
    @ViewById
    FloatingActionButton addTask;

    /**
     * {@link ListView ListView} for displaying the tasks.
     */
    @ViewById
    ListView myListView;

    /**
     * {@link TaskAdapter Adapter} for providing data to the {@link ListView listView}.
     */
    @Bean
    TaskAdapter adapter;

    @Bean
    UserDAOWrapper userDAOWrapper;

    @Bean
    TaskDAOWrapper taskDAOWrapper;

    @Pref
    UserPreferences_ userPreferences;

    @OptionsMenuItem
    MenuItem menuLogout;

    @RestService
    RestApi restApi;

    @Bean
    MyErrorHandler myErrorHandler;


    @AfterInject
    void setUpErrorHandler(){
        restApi.setRestErrorHandler(myErrorHandler);
    }

    @AfterViews
    @Background
    void checkUser() {
        if (!userPreferences.accessToken().exists()) {
            LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
            return;
        }

        initData();
    }

    /**
     * Loads tasks from the {@link android.content.SharedPreferences SharedPreferences}
     * and sets the adapter.
     */

    @ItemClick
    void myListViewItemClicked(Task task){
        final Gson gson = new Gson();
        TaskDetailActivity_.intent(this).extra("taskDetail",gson.toJson(task)).startForResult(EDIT_TASK_REQUEST_CODE);
    }

    @UiThread
    void initData() {
        myListView.setAdapter(adapter);
        adapter.setTaskDoneListener(new TaskDoneListener() {
            @Override
            public void taskDone(Task task) {
                updateTaskState(task);
            }
        });


        myListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    // button show
                    addTask.show();
                } else {
                    // button hide
                    addTask.hide();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        refreshTasks();
    }

    @Background
    public void updateTaskState(Task task) {
        task.setFinished(!task.isFinished());
        try {
            restApi.updateTask(task.getId(),task);
        } catch (RestClientException e) {
            e.printStackTrace();
            task.setFinished(!task.isFinished());
        }
        refreshList();
    }

    /**
     * Called when the {@link FloatingActionButton FloatingActionButton} is clicked.
     */
    @Click
    void addTask() {
        AddTaskActivity_.intent(this).startForResult(ADD_TASK_REQUEST_CODE);
    }

    /**
     * Called when the {@link AddTaskActivity AddTaskActivity} finishes.
     *
     * @param resultCode Indicates whether the activity was successful.
     * @param task         The new task.
     */
    @OnActivityResult(ADD_TASK_REQUEST_CODE)
    @Background
    void onResult(int resultCode, @OnActivityResult.Extra String task) {
        if (resultCode == RESULT_OK) {
            final Gson gson = new Gson();
            final Task newTask = gson.fromJson(task, Task.class);

            try {
                final Task newNewTask = restApi.createTask(newTask);
                onTaskCreated(newNewTask);
            } catch (RestClientException e) {
                Log.e(TAG, e.getMessage(),e);
            }
        }
    }

    @UiThread
    void onTaskCreated(Task task) {
        tasks.add(task);
        adapter.addTask(task);
    }

    @OnActivityResult(LOGIN_REQUEST_CODE)
    void onLogin(int resultCode, @OnActivityResult.Extra("token") String token) {
        if (resultCode == RESULT_OK) {
            userPreferences.accessToken().put(token);
            checkUser();
        }
    }


    @OptionsItem
    boolean menuLogout() {
        userPreferences.accessToken().remove();
        LoginActivity_.intent(this).startForResult(LOGIN_REQUEST_CODE);
        return true;
    }

    @OptionsItem
    boolean refresh() {
        refreshTasks();
        return true;
    }

    @Background
    void refreshTasks(){
        try {
            tasks = restApi.getAallTasks();
            refreshList();
        } catch (RestClientException e) {
            e.printStackTrace();
        }
    }

    @UiThread
    void refreshList() {
        if(tasks != null) {
            adapter.setTasks(tasks);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        refreshTasks();
    }
}
