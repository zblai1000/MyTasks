package com.bignerdranch.android.mytasks;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;
import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    //set EXTRA value for time
    public static final String EXTRA_TIME = "com.bignerdranch.android.mytasks.time";

    //set key for time data
    private static final String ARG_TIME = "time";

    //initiate Date object
    private Date date;

    //initiate TimePicker object
    private TimePicker mTimePicker;

    //create and set fragment arguments for time
    public static TimePickerFragment newInstance (Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, date);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //when Dialog is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //set calendar details to be the same as the calendar details when setting the date
        date = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        //display the time dialog
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_time, null);

        mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
        mTimePicker.setCurrentHour(calendar.get(Calendar.HOUR));
        mTimePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));

        //change the time from the date data
        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hour, int minute) {

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.set(Calendar.HOUR, hour);
                calendar.set(Calendar.MINUTE, minute);
                date.setTime(calendar.getTimeInMillis());

                getArguments().putSerializable(EXTRA_TIME, date);
            }
        });

        //set up AlertDialog to be displayed
        //when user click "ok", retrieve selected time and call sendResult().
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {

                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    //send data back to TaskFragment
    private void sendResult(int resultCode) {

        if(getTargetFragment() == null) {

            return;
        }
        Intent intent = new Intent();
        //puts date as an extra and call onActivityResults in TaskFragment
        intent.putExtra(EXTRA_TIME, date);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
