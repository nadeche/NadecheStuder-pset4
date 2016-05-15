package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;

/**
 * Created by Nadeche Studer
 *
 * This class holds the fields of a single toDoItem.
 */
class ToDoItem {

    private long id;             // holds the id from the corresponding database row
    private String task;        // holds the task to do
    private boolean isDone;     // holds whether a task is done(true) or not(false)
    private long listId;         // holds the table id of the list name the to do item belongs to

    public ToDoItem(long id, String task, long listId) {
        setId(id);
        setTask(task);
        setDone(false);
        setListId(listId);
    }

    // get and set id
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    // get and set isDone
    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public void setDone(boolean done, Context context) {
        setDone(done);

        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        toDoListDbHelper.update(this);
    }

    // get and set task
    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    // get and set listId
    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }
}
