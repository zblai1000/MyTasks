package com.bignerdranch.android.mytasks.database;

public class TaskDbSchema {

    public static final class TaskTable{

        public static final String NAME = "tasks";

        //describes the columns in the table "tasks"
        public static final class Cols{

            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String DESCRIPTION = "description";
            public static final String DONE = "done";
            public static final String REMOVE = "remove";
            public static final String RESPONSIBLE = "responsible";
        }
    }
}
