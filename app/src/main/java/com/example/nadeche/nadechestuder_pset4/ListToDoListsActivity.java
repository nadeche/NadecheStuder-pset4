package com.example.nadeche.nadechestuder_pset4;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Nadeche Studer
 *
 * This activity shows the list of to do lists saved in the database.
 * The user can add new list with the floating action button at the bottom.
 * The user can also delete a list by tapping and holding.
 */
public class ListToDoListsActivity extends AppCompatActivity {

    private ListView toDoListsListView;                 // holds the to do lists in display
    private ToDoManagerSingleton toDoManagerSingleton;  // holds all to do lists and their items as saved in the database
    private ListToDoListAdapter listToDoListAdapter;    // handles the display of the to do lists

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_to_do_lists);

        // initialize fields
        toDoManagerSingleton = ToDoManagerSingleton.getInstance();
        toDoManagerSingleton.readToDos(this);
        listToDoListAdapter = new ListToDoListAdapter(this, toDoManagerSingleton.getToDoListList());
        toDoListsListView = (ListView)findViewById(R.id.toDoListsListView);
        toDoListsListView.setAdapter(listToDoListAdapter);
        FloatingActionButton fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);

        // with tap on floating action button, open dialog so the user can enter a new list
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogAddList();
            }
        });

        // with tap on a list title, go to the next activity to show the items of that list
        toDoListsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goToList(position);
            }
        });

        // with tap and hold, open alert dialog to make sure the user wants to delete the list and its items
        toDoListsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                return alertDialogDeleteList(position);
            }
        });

    }

    /** Opens a dialog where the user can enter a new to do list.
     * The list will be added to the database and the singleton.
     * It checks if there is text to save. */
    private void dialogAddList() {

        // open dialog and set the layout
        final Dialog dialog = new Dialog(ListToDoListsActivity.this);
        dialog.setContentView(R.layout.dialog_add_list);
        dialog.setTitle(getText(R.string.dialog_message));

        Button addListButton = (Button)dialog.findViewById(R.id.addNewListButton);
        Button cancelButton = (Button) dialog.findViewById(R.id.dialogCancel);
        addListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // add the new list title
                EditText newListTitleEditText = (EditText) dialog.findViewById(R.id.newListTitleEditText);
                String newListTitle = newListTitleEditText.getText().toString();
                if(newListTitle.isEmpty()) {
                    // when the title is empty let the user know and close the dialog
                    Toast.makeText(ListToDoListsActivity.this, getText(R.string.toast_nothing_to_save),Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                }
                else {
                    toDoManagerSingleton.insertToDoList(ListToDoListsActivity.this, newListTitle);

                    listToDoListAdapter.notifyDataSetChanged();

                    dialog.dismiss();
                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // let the user know the action was canceled and close the dialog
                Toast.makeText(ListToDoListsActivity.this, getText(R.string.toast_canceled),Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /** Starts the ToDolListActivity.
     * Opens the to do list that was clicked. */
    private void goToList(int position) {
        ToDoList toDoList = (ToDoList) toDoListsListView.getItemAtPosition(position);

        Intent intent = new Intent(ListToDoListsActivity.this, ToDoListActivity.class);

        intent.putExtra("toDoListId",toDoList.getId() );
        startActivity(intent);
    }

    /** Displays a dialog to the user to ask if they want to delete the list they just taped and held.
     * If they are sure this deletes the list and its items and updates the listView.
     * If they don't want to delete the list it closes the dialog and does nothing  */
    private boolean alertDialogDeleteList(final int position){

        // open alert dialog and display dialog message
        AlertDialog.Builder alertDialogDeleteList = new AlertDialog.Builder(ListToDoListsActivity.this);
        alertDialogDeleteList.setMessage(getText(R.string.alert_dialog_delete_list_message));

        alertDialogDeleteList.setPositiveButton(getText(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                //  with a click on the yes button delete the list
                ToDoList deleteList = (ToDoList) toDoListsListView.getItemAtPosition(position);
                toDoManagerSingleton.deleteToDoList(ListToDoListsActivity.this, deleteList);

                // let the user know the list has been deleted
                Toast.makeText(ListToDoListsActivity.this, getText(R.string.toast_deleted), Toast.LENGTH_SHORT).show();

                listToDoListAdapter.notifyDataSetChanged();
            }
        });
        alertDialogDeleteList.setNegativeButton(getText(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // with a click on the no button let the user know the deletion has been canceled
                Toast.makeText(ListToDoListsActivity.this,getText(R.string.toast_canceled), Toast.LENGTH_SHORT).show();
                dialog.cancel();
            }
        });
        alertDialogDeleteList.show();

        return true;
    }
}
