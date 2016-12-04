package eu.execom.todolistgrouptwo.protocol;

import eu.execom.todolistgrouptwo.model.Task;



public interface TaskDoneListener {

    void taskDone(Task task);
}
