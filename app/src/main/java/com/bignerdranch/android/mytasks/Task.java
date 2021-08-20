package com.bignerdranch.android.mytasks;

import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

public class Task {

    private UUID mId;
    private String mTitle;
    private Date mDate;
    private String mDescription;
    private int mDone;
    private boolean mRemove = false;
    private String mResponsible;
    private Date mTime;

    public Task(){

        this(UUID.randomUUID());
    }

    public Task(UUID id){

        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setDescription(String description){

        mDescription = description;
    }

    public String getDescription(){

        return mDescription;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }


    public int isDone() {
        return mDone;
    }

    //1 -> done
    //0 -> not done
    public void setDone(int done) {
        mDone = done;
    }

    public void setRemove(boolean delete){

        mRemove = delete;
    }

    public boolean getRemove(){

        return mRemove;
    }

    public String getResponsible(){

        return mResponsible;
    }

    public void setResponsible(String responsible){

        mResponsible = responsible;
    }

    public String getPhotoFileName(){

        return "IMG_" + getId().toString() + ".jpg";
    }

    //compare values in list that store task and arrange them alphabetically.
    public static Comparator<Task> titleAZ = new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            return task1.getTitle().compareTo(task2.getTitle());
        }
    };

    //compare values in list that store task and arrange them by date
    //will be arrange starting from latest tasks
    public static Comparator<Task> date01 = new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            return task2.getDate().compareTo(task1.getDate());
        }
    };

    //compare values in list that store task and arrange them according to task done or not
    //tasks not done will be placed in front of the list
    public static Comparator<Task> done01 = new Comparator<Task>() {
        @Override
        public int compare(Task task1, Task task2) {
            return Integer.compare(task1.isDone(), task2.isDone());
        }
    };
}
