package com.example.nadeche.nadechestuder_pset4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * Created by Nadeche Studer
 *
 * This database helper creates a database with 2 tables, to hold to do lists and to hold to do items.
 * The lists table holds 2 columns: id and title.
 * The items table holds 4 columns: id, task, isDone, listId.
 * It holds the users to do tasks, whether the task is done or not and to which list it belongs.
 * With the creation of the database, one list with three tasks are created, with instruction on how to use the app.
 *
 * This database helper can also insert new rows in each table.
 * Read the entire database and pass it back.
 * Update existing rows in each table.
 * Delete an existing row in each table. When a to do list is deleted the corresponding items are also deleted.
 */
class ToDoListDbHelper extends SQLiteOpenHelper {

    // database information
    private static final String DB_NAME = "toDos.db";
    private static final int DB_VERSION = 1;

    // column name id for both tables
    private static final String _ID = "_id";

    // to do lists table name and column
    private static final String TABLE_NAME_LISTS = "toDoLists";
    private static final String TITLE_LISTS_COLUMN = "title";

    // to do items table name and column names
    private static final String TABLE_NAME_TO_DOS = "toDoItems";
    private static final String TASK_TO_DOS_COLUMN = "task";
    private static final String IS_DONE_TO_DOS_COLUMN = "isDone";
    private static final String LIST_ID_TO_DOS_COLUMN = "listId";

    // context that holds the activity's context in order to use recourse strings
    private final Context activityContext;

    public ToDoListDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        activityContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // create to do lists table
        String CREATE_TABLE_LISTS = "CREATE TABLE " + TABLE_NAME_LISTS + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TITLE_LISTS_COLUMN + " TEXT NOT NULL)";
        db.execSQL(CREATE_TABLE_LISTS);

        // create to do items table
        String CREATE_TABLE_TO_DOS = "CREATE TABLE " + TABLE_NAME_TO_DOS + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_TO_DOS_COLUMN + " TEXT NOT NULL, " +
                IS_DONE_TO_DOS_COLUMN + " INTEGER DEFAULT 0, " +
                LIST_ID_TO_DOS_COLUMN + " INTEGER NOT NULL)";
        db.execSQL(CREATE_TABLE_TO_DOS);

        // insert instruction list
        String INSERT_DUMMY_LIST = "INSERT INTO " + TABLE_NAME_LISTS + " (" + TITLE_LISTS_COLUMN + ") " +
                "VALUES('Instructions')";
        db.execSQL(INSERT_DUMMY_LIST);

        // get the id from the instruction list
        String query = "SELECT "+ _ID + " FROM " + TABLE_NAME_LISTS;
        Cursor cursor = db.rawQuery(query, null);
        long instructionListId = -1;
        if(cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(_ID);
            instructionListId = cursor.getLong(idColumnIndex);
        }
        cursor.close();

        // insert instructions on how to use the app
        db.execSQL("INSERT INTO " + TABLE_NAME_TO_DOS + "(" + TASK_TO_DOS_COLUMN + ", " + LIST_ID_TO_DOS_COLUMN + ") " +
                "VALUES('"+ activityContext.getText(R.string.db_instruction_entry_add) +"', " + instructionListId + ")," +
                "('" + activityContext.getText(R.string.db_instruction_entry_done) +"', " + instructionListId + ")," +
                "('" + activityContext.getText(R.string.db_instruction_entry_delete)+"', " + instructionListId + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TO_DOS);
        onCreate(db);
    }

    //----------------------- method involving both tables -----------------------//

    /** Reads all current rows of both tables and saves it in the ToDoList list(singleton) */
    public void readAll(List<ToDoList> toDoListList) {

        SQLiteDatabase db = getReadableDatabase();

        // get all information from the database, join titleListId from lists to listId from items, sort by titleListId and itemListId
        String query = "SELECT " + TABLE_NAME_TO_DOS + ".*, " + TITLE_LISTS_COLUMN + ", " + TABLE_NAME_LISTS + "." + _ID + " AS titleListId FROM " + TABLE_NAME_LISTS +
                " LEFT JOIN " + TABLE_NAME_TO_DOS + " ON " + TABLE_NAME_LISTS + "." + _ID +
                " = " + TABLE_NAME_TO_DOS + "." + LIST_ID_TO_DOS_COLUMN +
                " ORDER BY " + TABLE_NAME_LISTS + "." + _ID + ", " + TABLE_NAME_TO_DOS + "." + _ID;
        Cursor cursor = db.rawQuery(query, null);

        // clear the current toDoListList
        toDoListList.clear();

        if(cursor.moveToFirst()) {

            int _idColumnIndex = cursor.getColumnIndex(_ID);
            int taskColumnIndex = cursor.getColumnIndex(TASK_TO_DOS_COLUMN);
            int isDoneColumnIndex = cursor.getColumnIndex(IS_DONE_TO_DOS_COLUMN);
            int titleColumnIndex = cursor.getColumnIndex(TITLE_LISTS_COLUMN);
            int titleListId = cursor.getColumnIndex("titleListId");
            long lastListId = 0;
            ToDoList toDoList = null;

            // save the new items in the ToDoListList
            do {
                if(cursor.getLong(titleListId) > lastListId) {
                    // create new to do list
                    toDoList = new ToDoList(
                            cursor.getLong(titleListId),
                            cursor.getString(titleColumnIndex));

                    toDoListList.add(toDoList);
                }

                if (!cursor.isNull(_idColumnIndex)) {
                    // create new to do item
                    ToDoItem toDoItem = new ToDoItem(
                    cursor.getLong(_idColumnIndex),
                    cursor.getString(taskColumnIndex),
                    cursor.getLong(titleListId));
                    toDoItem.setDone(cursor.getInt(isDoneColumnIndex) == 1);

                    toDoList.getToDoList().add(toDoItem);
                }

                lastListId = cursor.getLong(titleListId);
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    //------------------- CUD methods for to do item table -------------------//

    /** Inserts a new row in the to do's table with corresponding listId, sets the is_done column to default false.
     * Returns the id of the new inserted row */
    public long insert(String task, long listId) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(TASK_TO_DOS_COLUMN, task);
        entry.put(LIST_ID_TO_DOS_COLUMN, listId);

        long rowId = db.insert(TABLE_NAME_TO_DOS, null, entry);

        db.close();

        return rowId;
    }

    /** Updates a row of the to do's table, corresponding with the passed toDoItem */
    public void update(ToDoItem toDoItem){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_TO_DOS_COLUMN, toDoItem.getTask());
        contentValues.put(IS_DONE_TO_DOS_COLUMN,toDoItem.isDone());

        db.update(TABLE_NAME_TO_DOS,contentValues,_ID + "=?" , new String[]{String.valueOf(toDoItem.getId())});

        db.close();
    }

    /** Deletes a row of the to do's table, corresponding with the passed toDoItem. */
    public void delete(ToDoItem toDoItem){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME_TO_DOS,_ID + "=?", new String[]{String.valueOf(toDoItem.getId())});

        db.close();
    }

    //------------------- methods for to do lists table -------------------//

    /** Inserts a new row in the to do lists table with a list title.
     * Returns the rowId of the new entry. */
    public  long insert(String listTitle) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(TITLE_LISTS_COLUMN, listTitle);

        long rowId = db.insert(TABLE_NAME_LISTS, null, entry);

        db.close();

        return rowId;
    }

    /** Deletes a row of the to do lists table, corresponding with the passed toDoList,
     *  and the rows with the corresponding listId in the to do's table. */
    public void delete(ToDoList toDoList){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME_TO_DOS, LIST_ID_TO_DOS_COLUMN + "=?", new String[]{String.valueOf(toDoList.getId())});

        db.delete(TABLE_NAME_LISTS, _ID + "=?", new String[]{String.valueOf(toDoList.getId())});

        db.close();
    }
}
