package com.bignerdranch.android.mytasks;

import android.support.v4.app.Fragment;

public class TaskListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){

        return new TaskListFragment();
    }
}
