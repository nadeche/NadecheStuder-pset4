package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * Main activity of this to do list app.
 * It shows to do tasks saved in a database.
 * The user can add new tasks with an editText at the bottom.
 * The user can also set tasks to done by tapping and delete them by tapping and holding.
 */
public class ToDoListActivity extends AppCompatActivity {

    private ToDoItemListAdapter toDoItemsListAdapter;    // handles the display of the to do items
    private ListView toDoListView;              // holds the to do items in display
    private ToDoList toDoList;            // hold the to do items in memory
    private ToDoManagerSingleton toDoManagerSingleton;
    private long listId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_do_list);

        Intent previousActivity = getIntent();
        listId = previousActivity.getLongExtra("toDoListId", -1);
        Log.d("listId", String.valueOf(listId));

        toDoManagerSingleton = ToDoManagerSingleton.getInstance();
        toDoList = toDoManagerSingleton.getToDoListById(listId);

        TextView listTitle = (TextView)findViewById(R.id.list_title);
        listTitle.setText(toDoList.getTitle());

        toDoItemsListAdapter = new ToDoItemListAdapter(this, toDoList.getToDoList());
        toDoListView = (ListView)findViewById(R.id.toDoItemsListView);
        toDoListView.setAdapter(toDoItemsListAdapter);


        // with short tap set the to do task to done or not done
        toDoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                toDoOrNotToDo(position);
            }
        });

        // with tap and hold open alert dialog to make sure the user wants to delete the to do task
        toDoListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return alertDialogToDelete(position);
            }
        });

    }

    /** Handle the click on the 'add' button to add a new to do task to the database and the listView.
     *  It also checks if there is something in the editText to save */
    public void addToDoItemButtonClick(View view) {

        // hide the keyboard
        InputMethodManager inputManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        View focusedView = getCurrentFocus();

        if(focusedView != null){
            inputManager.hideSoftInputFromWindow(focusedView.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }

        // get the new task text from the editText
        EditText newToDoItemEditText = (EditText)findViewById(R.id.newToDoItemEditText);
        String newToDoTask = newToDoItemEditText.getText().toString();

        if(!newToDoTask.isEmpty()) {

            // user has entered text so save it
            toDoList.insertToDoItem(ToDoListActivity.this, newToDoTask, listId);

            // let the user know the save was successful
            Toast.makeText(ToDoListActivity.this, getText(R.string.toast_saved), Toast.LENGTH_SHORT).show();

            // update the listView
            updateView();

            // clear editText
            newToDoItemEditText.setText("");
        }
        else {
            // let the user know there was no new task to save
            Toast.makeText(ToDoListActivity.this, getText(R.string.toast_nothing_to_save), Toast.LENGTH_SHORT).show();
        }

        // get the focus off the editText
        newToDoItemEditText.clearFocus();
    }

    /** Update the listView by reading the current database and notifying the list adapter */
    private void updateView() {
        toDoItemsListAdapter.notifyDataSetChanged();
    }

    /** Check if the taped item is set to done or not done and sat it to the opposite state */
    private void toDoOrNotToDo(int position) {

        ToDoItem toDoTask = (ToDoItem) toDoListView.getItemAtPosition(position);

        toDoTask.setDone(!toDoTask.isDone(), this);

        updateView();

        // display to the user what has changed
        if(toDoTask.isDone()) {
            Toast.makeText(this, getText(R.string.toast_done), Toast.LENGTH_SHORT).show();
        }
        else {
            Toast.makeText(this, getText(R.string.to_do), Toast.LENGTH_SHORT).show();
        }
    }

    /** Display a dialog to the user to ask if they want to delete the task they just taped and held.
     * If they are sure delete the task form the database and update the listView.
     * If they don't want to delete the task close the dialog and do nothing */
    private boolean alertDialogToDelete(final int position) {

        // open alert dialog and display dialog message
        AlertDialog.Builder alertDialogDeleteToDoItem = new AlertDialog.Builder(ToDoListActivity.this);
        alertDialogDeleteToDoItem.setMessage(getText(R.string.delete_alert_message));
        alertDialogDeleteToDoItem.setCancelable(false);

        alertDialogDeleteToDoItem.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // with a click on the yes button delete the task form the database
                ToDoItem deleteTask = (ToDoItem) toDoListView.getItemAtPosition(position);
                toDoList.deleteToDoItem(ToDoListActivity.this, deleteTask);

                // let the user know they task has been deleted
                Toast.makeText(ToDoListActivity.this, getText(R.string.toast_deleted), Toast.LENGTH_SHORT).show();

                // update the listView
                updateView();
            }
        });
        alertDialogDeleteToDoItem.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // with a click on the no button let the user know the deletion has been canceled
                Toast.makeText(ToDoListActivity.this,getText(R.string.toast_canceled), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        alertDialogDeleteToDoItem.show();

        return true;
    }
}
