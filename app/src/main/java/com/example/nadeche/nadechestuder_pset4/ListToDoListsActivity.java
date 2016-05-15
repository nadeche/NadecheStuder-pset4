package com.example.nadeche.nadechestuder_pset4;

import android.app.Dialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ListToDoListsActivity extends AppCompatActivity {

    //private ToDoListDbHelper toDoListDbHelper;  // handles all database interactions
    //private ToDoItemListAdapter toDoListAdapter;    // handles the display of the to do items
    private ListView toDoListsListView;         // holds the to do lists in display
    private ToDoManagerSingleton toDoManagerSingleton;
    private ListToDoListAdapter listToDoListAdapter;
    //private List<ToDoItem> toDoList;            // hold the to do items in memory

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_to_do_lists);

        toDoManagerSingleton = ToDoManagerSingleton.getInstance();
        toDoManagerSingleton.readToDos(this);

        listToDoListAdapter = new ListToDoListAdapter(this, toDoManagerSingleton.getToDoListList());

        toDoListsListView = (ListView)findViewById(R.id.toDoListsListView);
        toDoListsListView.setAdapter(listToDoListAdapter);

        FloatingActionButton fabAdd = (FloatingActionButton)findViewById(R.id.fab_add);
        //
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Dialog dialog = new Dialog(ListToDoListsActivity.this);
                dialog.setContentView(R.layout.dialog_add_list);
                dialog.setTitle(getText(R.string.add));

                Button addListButton = (Button)dialog.findViewById(R.id.addNewListButton);
                Button cancelButton = (Button) dialog.findViewById(R.id.dialogCancel);
                addListButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        EditText newListTitleEditText = (EditText) dialog.findViewById(R.id.newListTitleEditText);
                        String newListTitle = newListTitleEditText.getText().toString();
                        toDoManagerSingleton.insertToDoList(ListToDoListsActivity.this, newListTitle);

                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });

        toDoListsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                ToDoList toDoList = (ToDoList) toDoListsListView.getItemAtPosition(position);

                Intent intent = new Intent(ListToDoListsActivity.this, ToDoListActivity.class);

                intent.putExtra("toDoListId",toDoList.getId() );
                startActivity(intent);
            }
        });


    }
}
