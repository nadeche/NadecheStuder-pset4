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

        // create to do list table
        String CREATE_TABLE_TO_DOS = "CREATE TABLE " + TABLE_NAME_TO_DOS + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TASK_TO_DOS_COLUMN + " TEXT NOT NULL, " +
                IS_DONE_TO_DOS_COLUMN + " INTEGER DEFAULT 0, " +
                LIST_ID_TO_DOS_COLUMN + " INTEGER NOT NULL)";
        db.execSQL(CREATE_TABLE_TO_DOS);

        // insert dummy list
        String INSERT_DUMMY_LIST = "INSERT INTO " + TABLE_NAME_LISTS + " (" + TITLE_LISTS_COLUMN + ") " +
                "VALUES('Instructions')";
        db.execSQL(INSERT_DUMMY_LIST);
        Log.d("insert of dummy list", INSERT_DUMMY_LIST);

        // get id from first list
        String query = "SELECT "+ _ID + " FROM " + TABLE_NAME_LISTS;
        Cursor cursor = db.rawQuery(query, null);
        Log.d("select query", query);
        long dummyListId = -1;
        if(cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex(_ID);
            dummyListId = cursor.getLong(idColumnIndex);
        }
        cursor.close();
        Log.d("dummyListId", String.valueOf(dummyListId));

        // insert instructions on how to use the app
        db.execSQL("INSERT INTO " + TABLE_NAME_TO_DOS + "(" + TASK_TO_DOS_COLUMN + ", " + LIST_ID_TO_DOS_COLUMN + ") " +
                "VALUES('"+ activityContext.getText(R.string.db_instruction_entry_add) +"', " + dummyListId + ")," +
                "('" + activityContext.getText(R.string.db_instruction_entry_done) +"', " + dummyListId + ")," +
                "('" + activityContext.getText(R.string.db_instruction_entry_delete)+"', " + dummyListId + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LISTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TO_DOS);
        onCreate(db);
    }

    //---------------- method for both tables ----------------//

    /** Reads the current records of both tables and saves it in the ToDoList list */
    public void readAll(List<ToDoList> toDoListList) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + TABLE_NAME_TO_DOS + ".*, " + TITLE_LISTS_COLUMN + ", " + TABLE_NAME_LISTS + "." + _ID + " AS listId2 FROM " + TABLE_NAME_LISTS +
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
            int listId2ColumnIndex = cursor.getColumnIndex("listId2");
            long lastListId = 0;
            ToDoList toDoList = null;

            // save the new items in the ToDoListList
            do {
                if(cursor.getLong(listId2ColumnIndex) > lastListId) {
                    toDoList = new ToDoList(
                            cursor.getLong(listId2ColumnIndex),
                            cursor.getString(titleColumnIndex));

                    toDoListList.add(toDoList);
                    Log.d("new toDoList", String.valueOf(lastListId));
                }

                if (!cursor.isNull(_idColumnIndex)) {
                    ToDoItem toDoItem = new ToDoItem(
                    cursor.getLong(_idColumnIndex),
                    cursor.getString(taskColumnIndex),
                    cursor.getLong(listId2ColumnIndex));
                    toDoItem.setDone(cursor.getInt(isDoneColumnIndex) == 1);

                    toDoList.getToDoList().add(toDoItem);
                }

                lastListId = cursor.getLong(listId2ColumnIndex);
                Log.d("lastListId", String.valueOf(lastListId));
                Log.d("list name", cursor.getString(titleColumnIndex));
            } while(cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }


    //---------------- CRUD methods for to do item table ----------------//

    /** Inserts a new row in the to do's table with corresponding listId, sets the is_done column to default false */
    public long insert(String task, long listId) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(TASK_TO_DOS_COLUMN, task);
        entry.put(LIST_ID_TO_DOS_COLUMN, listId);

        long rowId = db.insert(TABLE_NAME_TO_DOS, null, entry);

        db.close();

        return rowId;
    }

    /** Reads the current to do's table and saves it in the ToDoItem list */
    public void read(List<ToDoItem> toDoList, long listId) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME_TO_DOS + " WHERE " + _ID + " = ?";
        Cursor toDoCursor = db.rawQuery(query, new String[]{String.valueOf(listId)});

        // clear the current toDoList
        toDoList.clear();

        if(toDoCursor.moveToFirst()) {

            int _idColumnIndex = toDoCursor.getColumnIndex(_ID);
            int taskColumnIndex = toDoCursor.getColumnIndex(TASK_TO_DOS_COLUMN);
            int isDoneColumnIndex = toDoCursor.getColumnIndex(IS_DONE_TO_DOS_COLUMN);
            int listIdColumnIndex = toDoCursor.getColumnIndex(LIST_ID_TO_DOS_COLUMN);

            // save the new items in the ToDoList
            do{
                ToDoItem toDoItem = new ToDoItem(
                        toDoCursor.getLong(_idColumnIndex),
                        toDoCursor.getString(taskColumnIndex),
                        toDoCursor.getLong(listIdColumnIndex));
                toDoItem.setDone(toDoCursor.getInt(isDoneColumnIndex) == 1);

                toDoList.add(toDoItem);

            } while(toDoCursor.moveToNext());
        }
        toDoCursor.close();
        db.close();
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

    /** Deletes a row of the to do's table, corresponding with the passed toDoItem */
    public void delete(ToDoItem toDoItem){

        SQLiteDatabase db = getWritableDatabase();

        db.delete(TABLE_NAME_TO_DOS,_ID + "=?", new String[]{String.valueOf(toDoItem.getId())});

        db.close();
    }

    //---------------- CRUD methods for to do lists table ----------------//

    /** Inserts a new row in the to do lists table and ads a list title*/
    public  long insert(String listTitle) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues entry = new ContentValues();
        entry.put(TITLE_LISTS_COLUMN, listTitle);

        long rowId = db.insert(TABLE_NAME_LISTS, null, entry);

        db.close();

        return rowId;
    }

    /** Reads the current to do lists table and saves it in the ToDoList list */
    public void read(List<ToDoList> toDoListList) {

        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT * FROM " + TABLE_NAME_LISTS;
        Cursor toDoCursor = db.rawQuery(query, null);

        // clear the current toDoListList
        toDoListList.clear();

        if(toDoCursor.moveToFirst()) {

            int _idColumnIndex = toDoCursor.getColumnIndex(_ID);
            int titleColumnIndex = toDoCursor.getColumnIndex(TITLE_LISTS_COLUMN);

            // save the new items in the ToDoList
            do{
                ToDoList toDoList = new ToDoList(
                        toDoCursor.getLong(_idColumnIndex),
                        toDoCursor.getString(titleColumnIndex));

                toDoListList.add(toDoList);

            } while(toDoCursor.moveToNext());
        }
        toDoCursor.close();
        db.close();
    }

    /** Updates a row of the to do lists table, corresponding with the passed toDoList */
    public void update(ToDoList toDoList){

        SQLiteDatabase db = getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE_LISTS_COLUMN, toDoList.getTitle());

        db.update(TABLE_NAME_LISTS,contentValues,_ID + "=?" , new String[]{String.valueOf(toDoList.getId())});

        db.close();
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
