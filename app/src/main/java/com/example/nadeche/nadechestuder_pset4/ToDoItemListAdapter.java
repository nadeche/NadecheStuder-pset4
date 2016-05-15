package com.example.nadeche.nadechestuder_pset4;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This custom arrayAdapter adapts ToDoItems to a listView.
 * It checks if a ToDoItem is done or not and sets the corresponding textView attributes.
 */
class ToDoItemListAdapter extends ArrayAdapter <ToDoItem> {

    public ToDoItemListAdapter(Context context, List<ToDoItem> toDoList) {
        super(context, R.layout.list_item, toDoList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater listItemInflater = LayoutInflater.from(getContext());

        View listItemView = listItemInflater.inflate(R.layout.list_item, parent, false);

        ToDoItem toDoItem = getItem(position);

        // set task from the toDoItem to a textView
        TextView toDoTaskTextView = (TextView)listItemView.findViewById(R.id.toDoTextView);
        toDoTaskTextView.setText(toDoItem.getTask());

        if(toDoItem.isDone()){
            // set text to gray and strike through when a toDoItem is done
            toDoTaskTextView.setTextColor(Color.GRAY);
            toDoTaskTextView.setPaintFlags(toDoTaskTextView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            // set text to black and no strike through when a toDoItem is not done
            toDoTaskTextView.setTextColor(Color.BLACK);
            toDoTaskTextView.setPaintFlags(toDoTaskTextView.getPaintFlags()&(~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        return listItemView;

    }
}
