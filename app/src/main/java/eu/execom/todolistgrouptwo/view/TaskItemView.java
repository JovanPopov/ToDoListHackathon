package eu.execom.todolistgrouptwo.view;

import android.content.Context;
import android.graphics.Paint;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.ViewById;

import eu.execom.todolistgrouptwo.R;
import eu.execom.todolistgrouptwo.model.Task;
import eu.execom.todolistgrouptwo.protocol.TaskDoneListener;

/**
 * Represents a {@link android.view.View view} for one item in a list.
 */
@EViewGroup(R.layout.view_item_task)
public class TaskItemView extends LinearLayout {


    @ViewById
    TextView title;

    @ViewById
    TextView description;

    @ViewById
    CheckBox taskDone;

    public TaskItemView(Context context) {
        super(context);
    }

    /**
     * Binds the task model to its view.
     *
     * @param task The model.
     * @return The view.
     */
    public TaskItemView bind(final Task task) {
        title.setText(task.getTitle());
        description.setText(task.getDescription());
        if(task.isFinished()) {
            taskDone.setChecked(true);
            title.setPaintFlags(title.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }else{
            taskDone.setChecked(false);
            title.setPaintFlags(title.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return this;
    }



    public CheckBox getTaskDone() {
        return taskDone;
    }
}
