package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This class holds the fields of a to do list.
 * It can insert and delete a ToDoItem in its list of ToDoItems and the database.
 */
public class ToDoList {

    private long id;                                        // holds the id of the list
    private String title;                                   // holds the name of the to do list
    private List<ToDoItem> toDoList = new ArrayList<>();    // holds the to do items of the to do list

    public ToDoList(long id, String title) {
        setId(id);
        setTitle(title);
    }

    // get and set the title
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // get and set the toDoList
    public List<ToDoItem> getToDoList() {
        return toDoList;
    }

    // get and set the id
    public long getId(){
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    /** Inserts a ToDoItem to its list of ToDoItems and in the database */
    public void insertToDoItem(Context context, String task, long listId) {

        // database insert
        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        long itemId= toDoListDbHelper.insert(task, listId);

        // list insert
        ToDoItem toDoItem = new ToDoItem(itemId, task, listId);
        toDoList.add(toDoItem);
    }

    /** Deletes a ToDoItem to its list of ToDoItems and in the database */
    public void deleteToDoItem(Context context, ToDoItem toDoItem) {

        // database delete
        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        toDoListDbHelper.delete(toDoItem);

        // list delete
        toDoList.remove(toDoItem);
    }
}
