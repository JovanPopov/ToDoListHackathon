package eu.execom.todolistgrouptwo.activity;

import android.content.Intent;
import android.os.PersistableBundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.google.gson.Gson;

import org.androidannotations.annotations.AfterTextChange;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.model.Task;

/**
 * {@link AppCompatActivity Activity} for creating a new task.
 */
@EActivity(R.layout.activity_add_task)
public class AddTaskActivity extends AppCompatActivity {



    /**
     * Title input {@link TextInputEditText TextInputEditText}.
     */
    @ViewById
    TextInputEditText title;

    /**
     * Description input {@link TextInputEditText TextInputEditText}.
     */
    @ViewById
    TextInputEditText description;

    @ViewById
    TextInputLayout inputError;

    /**
     * Called when the Save Task {@link android.widget.Button Button} is clicked.
     * Sets the result (the new task) so that the {@link HomeActivity HomeActivity} can read it.
     */
    @Click
    void saveTask() {

        String newTitle = title.getText().toString();

        if(!newTitle.equals("")) {
            final Task task = new Task(newTitle,
                    description.getText().toString());
            final Intent intent = new Intent();
            final Gson gson = new Gson();
            intent.putExtra("task", gson.toJson(task));
            setResult(RESULT_OK, intent);
            finish();
        }else{
            inputError.setError(getString(R.string.emptyError));
        }
    }

    @AfterTextChange(R.id.title)
    void removeError(TextView hello) {
        inputError.setErrorEnabled(false);

    }


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        getActionBar().setHomeButtonEnabled(true);
    }
}
