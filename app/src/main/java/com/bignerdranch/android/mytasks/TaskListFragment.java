package com.bignerdranch.android.mytasks;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class TaskListFragment extends Fragment {

    private static final String SAVED_SUBTITLE_VISIBLE = "subtitle";

    private RecyclerView mTaskRecyclerView;
    private TaskAdapter mAdapter;
    private boolean mSubtitleVisible;
    public List<Task> mTasks;


    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    //to find the RecyclerView in the layout file and then hook up the view to the fragment
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View view = inflater.inflate(R.layout.fragment_task_list, container, false);

        mTaskRecyclerView = (RecyclerView) view.findViewById(R.id.task_recycler_view);
        mTaskRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (savedInstanceState != null){

            mSubtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE);
        }
        updateUI();
        return view;
    }

    @Override
    public void onResume(){

        super.onResume();
        updateUI();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){

        super.onSaveInstanceState(outState);
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, mSubtitleVisible);
    }

    //create the OptionsMenu
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_task_list, menu);

        MenuItem subtitleItem = menu.findItem(R.id.show_subtitle);

        //set the title of the subtitleItem based on the visibility of the mSubtitleVisible
        if (mSubtitleVisible){

            subtitleItem.setTitle(R.string.hide_subtitle);
        } else{

            subtitleItem.setTitle(R.string.show_subtitle);
        }
    }

    //manage the option selected by user on the MenuItem
    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch (item.getItemId()){

            case R.id.new_task:

                Task task = new Task();
                TaskLab.get(getActivity()).addTask(task);
                Intent intent = TaskPagerActivity.newIntent(getActivity(), task.getId());
                startActivity(intent);
                return true;

            case R.id.show_subtitle:
                mSubtitleVisible = !mSubtitleVisible;
                getActivity().invalidateOptionsMenu();
                updateSubtitle();
                return true;

            case R.id.delete_all_tasks:

                TaskLab.get(getActivity()).deleteAllTasks();
                updateUI();
                return true;

            case R.id.sort_tasks_title:
                Collections.sort(mTasks, Task.titleAZ);
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_tasks_date:
                Collections.sort(mTasks, Task.date01);
                mAdapter.notifyDataSetChanged();
                return true;

            case R.id.sort_tasks_done:
                Collections.sort(mTasks, Task.done01);
                mAdapter.notifyDataSetChanged();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //update the count of the task to be displayed in the subtitle
    private void updateSubtitle(){

        TaskLab taskLab = TaskLab.get(getActivity());
        int taskCount = taskLab.getTasks().size();
        String subtitle = getString(R.string.subtitle_format, taskCount);

        if(!mSubtitleVisible){

            subtitle = null;
        }

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setSubtitle(subtitle);
    }

    //connect Adapter to RecyclerView
    //update the UI of TaskListFragment
    public void updateUI(){

        TaskLab taskLab = TaskLab.get(getActivity());
        List<Task> tasks = taskLab.getTasks();

        if(mAdapter == null){

            mAdapter = new TaskAdapter(tasks);
            mTaskRecyclerView.setAdapter(mAdapter);

        } else {
            mAdapter.setTasks(tasks);
            mAdapter.notifyDataSetChanged();
        }

        updateSubtitle();


    }

    //ViewHolder to inflate layout
    private class TaskHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener{

        private TextView mTitleTextView;
        private TextView mDateTextView;
        private ImageView mDoneImageView;
        private TextView mTimeTextView;
        private Time time = new Time();
        private Task mTask;

        //binding list items into ViewHolder
        public TaskHolder(LayoutInflater inflater, ViewGroup parent){

            super(inflater.inflate(R.layout.list_item_task, parent, false));
            itemView.setOnClickListener(this);

            mTitleTextView = (TextView) itemView.findViewById(R.id.task_title);
            mDateTextView = (TextView) itemView.findViewById(R.id.task_date);
            mDoneImageView = (ImageView) itemView.findViewById(R.id.task_done);
            mTimeTextView = (TextView) itemView.findViewById(R.id.task_time);
        }


        public void bind(Task task){

            mTask = task;
            mTitleTextView.setText(mTask.getTitle());
            mDateTextView.setText(String.format("%1$td %1$tb %1$tY", mTask.getDate()));
            time.set(mTask.getDate().getTime());
            String timeFormat = time.format("%I:%M %p");
            mTimeTextView.setText(timeFormat);
            //display the "tick" image on the task(s) that is(are) done
            mDoneImageView.setVisibility(task.isDone() == 1 ? View.VISIBLE : View.GONE);

            //to retrieve current time of system
            Date currentDate = Calendar.getInstance().getTime();

            //set the title of the task to red if:
                //incomplete (not done) and
                // due to today or overdue
            mTitleTextView.setTextColor(currentDate.compareTo(mTask.getDate()) > 0 && mTask.isDone() == 0
                                          ? Color.RED : Color.rgb(110, 88, 71));

        }

        //call the Fragment.startActivity(Intent) method, which calls the corresponding Actiivty method
        // behind the scenes.
        @Override
        public void onClick(View view){

            Intent intent = TaskPagerActivity.newIntent(getActivity(), mTask.getId());
            startActivity(intent);
        }
    }

    private class TaskAdapter extends RecyclerView.Adapter<TaskHolder>{

        public TaskAdapter(List<Task> tasks){
            mTasks = tasks;
        }

        //onCreateViewHolder is called by RecyclerView when it needs a new ViewHolder to display
        //an item with
        @Override
        public TaskHolder onCreateViewHolder(ViewGroup parent, int viewType){

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            return new TaskHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(TaskHolder holder, int position){

            Task task = mTasks.get(position);
            holder.bind(task);
        }

        @Override
        public int getItemCount(){

            return mTasks.size();
        }

        public void setTasks(List<Task> tasks) {

            mTasks = tasks;
        }
    }
}
































