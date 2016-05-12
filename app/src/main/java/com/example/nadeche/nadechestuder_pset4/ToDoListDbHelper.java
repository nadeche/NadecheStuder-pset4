package com.example.nadeche.nadechestuder_pset4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This database helper creates a database with 3 columns: id, task, isDone.
 * It holds the users to do tasks and whether the task is done or not.
 * With the creation of the database are three tasks created with instruction on how to use the app.
 *
 * This database helper can also insert new rows in the database.
 * Read the entire database and pass it back as a ToDoItem list object.
 * Update existing rows in the database by passing it a ToDoItem.
 * Delete an existing row in the database by passing it a ToDoItem.
 */
class ToDoListDbHelper extends SQLiteOpenHelper {

    // database and table information
    private static final String DB_NAME = "toDos.db";
    private static final int DB_VERSION = 3;
    private static final String TABLE_NAME = "toDoList";

    // column names
    private static final String _ID = "_id";
    private static final String TASK_COLUMN = "task";
    private static final String IS_DONE = "isDone";

    // context that holds the activity's context in order to use recourse strings
    private final Context activityContext;

    public ToDoListDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        activityContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create table
        String CREATE_DB = "CREATE TABLE " + TABLE_NAME + "(" +
                _ID +" INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_COLUMN +" TEXT NOT NULL, " +
                IS_DONE + " INTEGER DEFAULT 0)";
        db.execSQL(CREATE_DB);

        // insert instructions on how to use the app
        db.execSQL("INSERT INTO " + TABLE_NAME + "(" + TASK_COLUMN + ") " +
                "VALUES('"+ activityContext.getText(R.string.db_instruction_entry_add)+"')," +
                "('" + activityContext.getText(R.string.db_instruction_entry_done) + "')," +
                "('" + activityContext.getText(R.string.db_instruction_entry_delete)+"')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    /** Inserts a new row in the database, sets the is_done column to default false */
    public void insert(String task) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(TASK_COLUMN, task);

        db.insert(TABLE_NAME, null, entry);

        db.close();
    }

    /** Reads the current database and saves it in the ToDOItem list */
    public void read(List<ToDoItem> toDoList) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor toDoCursor = db.rawQuery(query, null);

        // clear the current toDoList
        toDoList.clear();

        if(toDoCursor.moveToFirst()) {

            int _idColumnIndex = toDoCursor.getColumnIndex(_ID);
            int taskColumnIndex = toDoCursor.getColumnIndex(TASK_COLUMN);
            int isDoneColumnIndex = toDoCursor.getColumnIndex(IS_DONE);

            // save the new items in the ToDoList
            do{
                ToDoItem toDoItem = new ToDoItem();
                toDoItem.id = toDoCursor.getInt(_idColumnIndex);
                toDoItem.task = toDoCursor.getString(taskColumnIndex);
                toDoItem.isDone = toDoCursor.getInt(isDoneColumnIndex) == 1;

                toDoList.add(toDoItem);

            } while(toDoCursor.moveToNext());
        }
        toDoCursor.close();
        db.close();
    }

    /** Updates a row of the database, corresponding with the passed toDoItem */
    public void update(ToDoItem toDoItem){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_COLUMN, toDoItem.task);
        contentValues.put(IS_DONE,toDoItem.isDone);

        db.update(TABLE_NAME,contentValues,_ID + "=?" , new String[]{String.valueOf(toDoItem.id)});

        db.close();
    }

    /** Deletes a row of the database, corresponding with the passed toDoItem */
    public void delete(ToDoItem toDoItem){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME,_ID + "=?", new String[]{String.valueOf(toDoItem.id)});

        db.close();
    }
}
