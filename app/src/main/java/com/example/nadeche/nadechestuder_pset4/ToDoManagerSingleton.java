package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This class is a singleton and holds a list of ToDoLists which holds a list of ToDoItems.
 * It can read all lists and items saved in the database.
 * It can return a single ToDoList.
 * It can insert and delete a ToDoList in its list of To do lists and the database.
 */
public class ToDoManagerSingleton {

    private List<ToDoList> toDoListList;    // holds the list of to do lists

    private static ToDoManagerSingleton toDoManager = new ToDoManagerSingleton();

    private ToDoManagerSingleton () {
        toDoListList = new ArrayList<>();
    }

    public static ToDoManagerSingleton getInstance() {
        return toDoManager;
    }

    /** Gets the list of to do lists */
    public List<ToDoList> getToDoListList() {
        return toDoListList;
    }

    /** Reads all to do lists and items form the database */
    public void readToDos(Context context) {
        ToDoListDbHelper dbHelper = new ToDoListDbHelper(context);
        dbHelper.readAll(toDoListList);
    }

    /** Returns a list of to do items with the corresponding listId */
    public ToDoList getToDoListById(long listId) {

        for (ToDoList toDoList : toDoListList) {

            if(toDoList.getId() == listId) {
               return toDoList;
            }
        }

        return null;
    }

    /** Inserts a ToDoList to its list of ToDoLists and in the database */
    public void insertToDoList(Context context, String title) {

        // insert in to database
        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        long listId = toDoListDbHelper.insert(title);

        // insert into list of lists
        ToDoList toDoList = new ToDoList(listId, title);
        toDoListList.add(toDoList);

    }

    /** Deletes a ToDoList from its list of ToDoLists and in the database */
    public void deleteToDoList(Context context, ToDoList toDoList) {

        // delete from database
        ToDoListDbHelper toDoListDbHelper = new ToDoListDbHelper(context);
        toDoListDbHelper.delete(toDoList);

        // delete from list of lists
        toDoListList.remove(toDoList);
    }
}
