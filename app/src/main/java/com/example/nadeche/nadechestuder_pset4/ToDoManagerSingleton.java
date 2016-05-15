package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadeche Studer
 */
public class ToDoManagerSingleton {

    // instance
    private static ToDoManagerSingleton toDoManager = new ToDoManagerSingleton();

    private List<ToDoList> toDoListList; // holds the list of to do lists

    /** constructs the to do manager singleton */
    private ToDoManagerSingleton () {
        toDoListList = new ArrayList<>();
    }

    /** gets instance of the ToDoManagerSingleton */
    public static ToDoManagerSingleton getInstance() {
        return toDoManager;
    }

    /** gets the list of to do lists */
    public List<ToDoList> getToDoListList() {
        return toDoListList;
    }

    /** sets the list of to do lists */
    public void setToDoListList(List<ToDoList> toDoListList) {
        this.toDoListList = toDoListList;
    }

    /** Reads all to do items and lists form the database*/
    public void readToDos(Context context) {
        ToDoListDbHelper dbHelper = new ToDoListDbHelper(context);
        dbHelper.readAll(toDoListList);
    }

    /** Returns the list of to do items of any particular to do list*/
    public ToDoList getToDoListById(long listId) {

        for (ToDoList toDoList : toDoListList) {

            if(toDoList.getId() == listId) {
               return toDoList;
            }
        }

        return null;
    }

    public void insertToDoList(Context context, String title) {

        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        long listId = toDoListDbHelper.insert(title);

        ToDoList toDoList = new ToDoList(listId, title);
        toDoListList.add(toDoList);

    }
}
