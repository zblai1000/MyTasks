package com.bignerdranch.android.mytasks.database;

import android.database.Cursor;
import android.database.CursorWrapper;
import java.util.Date;
import java.util.UUID;

import com.bignerdranch.android.mytasks.Task;
import com.bignerdranch.android.mytasks.database.TaskDbSchema.TaskTable;

public class TaskCursorWrapper extends CursorWrapper {

    public TaskCursorWrapper(Cursor cursor){

        super(cursor);
    }


    public Task getTask(){

        //retrieve data from database
        String uuidString = getString(getColumnIndex(TaskTable.Cols.UUID));
        String title = getString(getColumnIndex(TaskTable.Cols.TITLE));
        long date = getLong(getColumnIndex(TaskTable.Cols.DATE));
        String description = getString(getColumnIndex(TaskTable.Cols.DESCRIPTION));
        int isDone = getInt(getColumnIndex(TaskTable.Cols.DONE));
        int remove = getInt(getColumnIndex(TaskTable.Cols.REMOVE));
        String responsible = getString(getColumnIndex(TaskTable.Cols.RESPONSIBLE));

        //convert data retrieved from the database into model objects
        Task task = new Task(UUID.fromString(uuidString));
        task.setTitle(title);
        task.setDate(new Date(date));
        task.setDescription(description);
        task.setDone(isDone);
        task.setRemove(remove != 0);
        task.setResponsible(responsible);

        return task;
    }
}
