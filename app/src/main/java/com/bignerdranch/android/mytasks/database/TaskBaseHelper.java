package com.bignerdranch.android.mytasks.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.mytasks.database.TaskDbSchema.TaskTable;

public class TaskBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "taskBase.db";

    public TaskBaseHelper(Context context){

        super(context, DATABASE_NAME, null, VERSION);
    }

    //build the table in the database that store each task value
    @Override
    public void onCreate(SQLiteDatabase db){

        db.execSQL("create table " + TaskTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                TaskTable.Cols.UUID + ", " +
                TaskTable.Cols.TITLE + ", " +
                TaskTable.Cols.DATE + ", " +
                TaskTable.Cols.DESCRIPTION + ", " +
                TaskTable.Cols.DONE + ", " +
                TaskTable.Cols.REMOVE + ", " +
                TaskTable.Cols.RESPONSIBLE +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){


    }
}
