package com.bignerdranch.android.mytasks;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import java.util.UUID;

public class TaskActivity extends SingleFragmentActivity {


    private static final String EXTRA_TASK_ID = "com.bignerdranch.android.mytasks.task_id";

    public static Intent newIntent(Context packageContext, UUID taskId){

        Intent intent = new Intent(packageContext, TaskActivity.class);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        return intent;
    }

    @Override
    protected Fragment createFragment(){

        UUID taskId = (UUID) getIntent().getSerializableExtra(EXTRA_TASK_ID);
        return TaskFragment.newInstance(taskId);
    }
}
