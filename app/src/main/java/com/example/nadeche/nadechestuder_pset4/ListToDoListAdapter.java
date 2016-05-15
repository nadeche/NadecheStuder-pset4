package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This custom arrayAdapter adapts to do lists to a listView.
 */
public class ListToDoListAdapter extends ArrayAdapter <ToDoList> {

    public ListToDoListAdapter(Context context, List <ToDoList> listToDoList){
        super(context, R.layout.list_item, listToDoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listItemInflater = LayoutInflater.from(getContext());

        View listItemView = listItemInflater.inflate(R.layout.list_item, parent, false);

        ToDoList toDoList = getItem(position);

        // set the title of the list to a textView
        TextView toDoListTextView = (TextView) listItemView.findViewById(R.id.toDoTextView);
        toDoListTextView.setText(toDoList.getTitle());

        return listItemView;
    }
}
