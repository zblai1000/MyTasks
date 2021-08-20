package com.bignerdranch.android.mytasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.bignerdranch.android.mytasks.database.TaskBaseHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.bignerdranch.android.mytasks.database.TaskCursorWrapper;
import com.bignerdranch.android.mytasks.database.TaskDbSchema.TaskTable;

public class TaskLab {

    private static TaskLab sTaskLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    //if instance already exist, return instance
    //if instance does not exist, call the contructor to create instance
    public static TaskLab get(Context context){

        if (sTaskLab == null){

            sTaskLab = new TaskLab(context);
        }
        return sTaskLab;
    }

    private TaskLab(Context context){

        mContext = context.getApplicationContext();
        mDatabase = new TaskBaseHelper(mContext).getWritableDatabase();
    }

    //called when user click on the "+" button to add task
    public void addTask(Task t){

            ContentValues values = getContentValues(t);
            mDatabase.insert(TaskTable.NAME, null, values);

    }

    //query all tasks, walk the cursor and populate the Task list
    public List<Task> getTasks(){

        List<Task> tasks = new ArrayList<>();
        TaskCursorWrapper cursor = queryTasks(null, null);

        try {
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){

                tasks.add(cursor.getTask());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return tasks;
    }

    //pass UUID into queryTasks() to search for data in table
    //retrieve and update values stored in cursor
    public Task getTask(UUID id){

        TaskCursorWrapper cursor = queryTasks(
                TaskTable.Cols.UUID + " = ?",
                new String[] { id.toString()}
        );

        try {

            if (cursor.getCount() == 0){

                return null;
            }
            cursor.moveToFirst();
            return cursor.getTask();

        } finally {

            cursor.close();
        }
    }

    //retrieve file to be sent back to TaskFragment
    public File getPhotoFile(Task task){

        File filesDir = mContext.getFilesDir();
        return new File(filesDir, task.getPhotoFileName());
    }

    //update TaskListFragment every time the page is loaded
    public void updateTask(Task task){

        //get UUID and convert to string to be stored in this variable
        String uuidString = task.getId().toString();
        //get the contents of the task selected
        ContentValues values = getContentValues(task);
        //convert the contents of the task into string
        String tempValues = values.toString();

        //check if title is empty
            //null is check for empty
            //the blank space is check for empty after updating then deleting the title
        //if title is empty then delete the task selected
        //this is to ensure a task with an empty title will not be saved into the database
        if (tempValues.contains("title=null") || tempValues.contains("title= ")) {
            mDatabase.delete(TaskTable.NAME,
                    TaskTable.Cols.UUID +
                            " = ? ", new String[]{uuidString});
        }
        else{

            //check if user have removed the button
            //delete the task if user click on the remove button
            //remove will be set to 1 if user click on the remove button
            if(tempValues.contains("remove=1")){
                mDatabase.delete(TaskTable.NAME, TaskTable.Cols.UUID + " = ?",
                        new String[] {uuidString});
            }
            //if title is not empty and user did not delete the task,
            //update the task selected for the changes to be reflected on the list
            else{
                mDatabase.update(TaskTable.NAME, values,
                        TaskTable.Cols.UUID + " = ?",
                        new String[]{uuidString});
            }
        }
    }



    private TaskCursorWrapper queryTasks(String whereClause, String[] whereArgs){

        Cursor cursor = mDatabase.query(
                TaskTable.NAME,
                null, //columns - null selects all columns
                whereClause,
                whereArgs,
                null, //groupBy
                null, //having
                null //orderBy
        );
        return new TaskCursorWrapper(cursor);
    }

    //delete all rows in the TaskTable
    public void deleteAllTasks(){

        mDatabase.execSQL("delete from "+ TaskTable.NAME);
    }

    //store values of task into TaskTable
    private static ContentValues getContentValues(Task task){

        ContentValues values = new ContentValues();

        values.put(TaskTable.Cols.UUID, task.getId().toString());
        values.put(TaskTable.Cols.TITLE, task.getTitle());
        values.put(TaskTable.Cols.DATE, task.getDate().getTime());
        values.put(TaskTable.Cols.DESCRIPTION, task.getDescription());
        values.put(TaskTable.Cols.DONE, task.isDone() == 1 ? 1: 0);
        values.put(TaskTable.Cols.REMOVE, task.getRemove() ? 1: 0);
        values.put(TaskTable.Cols.RESPONSIBLE, task.getResponsible());

        return values;
    }





























}
