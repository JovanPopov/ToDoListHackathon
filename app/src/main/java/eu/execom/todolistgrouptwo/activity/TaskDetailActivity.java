package eu.execom.todolistgrouptwo.activity;

import android.content.DialogInterface;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.rest.spring.annotations.RestService;
import org.springframework.web.client.RestClientException;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.api.RestApi;
import eu.execom.todolistgrouptwo.api.errorhandler.MyErrorHandler;
import eu.execom.todolistgrouptwo.model.Task;



@EActivity(R.layout.activity_add_task)
@OptionsMenu(R.menu.detail_menu_items)
public class TaskDetailActivity extends AppCompatActivity {

    public static final String TAG = TaskDetailActivity.class.getSimpleName();

    @OptionsMenuItem
    MenuItem taskDoneMenu;

    @Extra
    String taskDetail;

    Task task;

    @ViewById
    TextInputEditText title;

    @ViewById
    TextInputEditText description;

    @RestService
    RestApi restApi;

    @ViewById
    TextInputLayout inputError;

    @Bean
    MyErrorHandler myErrorHandler;

    @AfterInject
    void setUpErrorHandler(){
        restApi.setRestErrorHandler(myErrorHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Gson gson = new Gson();
        task = gson.fromJson(taskDetail, Task.class);
        setFields();

        return true;
    }


    void setFields(){
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        if(task.isFinished()) {
            taskDoneMenu.setIcon(R.drawable.ic_check_box_white_24dp);
        }else{
            taskDoneMenu.setIcon(R.drawable.ic_check_box_outline_blank_white_24dp);
        }
    }



    @Background
    @Click
    void saveTask() {

        String newTitle = title.getText().toString();
        String newDesc = description.getText().toString();

        task.setTitle(newTitle);
        task.setDescription(newDesc);


        if(!newTitle.equals("")) {

            try {
                task = restApi.updateTask(task.getId(), task);
                notifyUser();
            } catch (RestClientException e) {
                Log.e(TAG, e.getMessage(), e);
            }

        }else{
            showError();

        }
    }

    @UiThread
    public void showError() {
        inputError.setErrorEnabled(true);
        inputError.setError(getString(R.string.emptyError));
    }

    @UiThread
    public void notifyUser() {
        setFields();
        Toast.makeText(this, R.string.taskUpdated, Toast.LENGTH_SHORT).show();
    }

    @OptionsItem
    boolean taskDoneMenu() {
        task.setFinished(!task.isFinished());
        saveTask();
        return true;
    }


    @OptionsItem
    boolean taskDeleteMenu() {

        AlertDialog.Builder alert = new AlertDialog.Builder(
                this);
        alert.setTitle(R.string.confirm_delete);
        alert.setMessage(R.string.delete_text);
        alert.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteTask();
                dialog.dismiss();
            }
        });
        alert.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
            }
        });

        alert.show();

        return true;
    }

    @Background
    public void deleteTask() {
        try {
            restApi.deleteTask(task.getId());
            finish();
        } catch (RestClientException e) {
            e.printStackTrace();
            notifyUserDelete();
        }
    }

    @UiThread
    public void notifyUserDelete() {
        Toast.makeText(this, R.string.delete_error, Toast.LENGTH_SHORT).show();
    }

    @AfterTextChange(R.id.title)
    void removeError() {
        inputError.setErrorEnabled(false);
    }


    @OptionsItem(android.R.id.home)
    void homeSelected() {
        onBackPressed();
    }
}
