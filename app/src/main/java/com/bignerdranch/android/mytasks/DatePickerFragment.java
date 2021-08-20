package com.bignerdranch.android.mytasks;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DatePickerFragment extends DialogFragment {

    //set EXTRA value for date
    public static final String EXTRA_DATE = "com.bignerdranch.android.mytasks.date";

    //set key for date data
    private static final String ARG_DATE = "date";

    //initiate DatePicker object
    private DatePicker mDatePicker;

    //create and set fragment arguments for date
    public static DatePickerFragment newInstance(Date date) {

        Bundle args = new Bundle();
        args.putSerializable(ARG_DATE, date);


        DatePickerFragment fragment = new DatePickerFragment();
        fragment.setArguments(args);
        return fragment;

    }

    //when Dialog is created
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //set Calendar details to the system's current date and time
        Date date = (Date) getArguments().getSerializable(ARG_DATE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        //display the calendar
        View v = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_date, null);

        mDatePicker = (DatePicker) v.findViewById(R.id.dialog_date_picker);
        mDatePicker.init(year, month, day, null);

        //set up AlertDialog to be displayed
        //when user click "ok", retrieve selected date and call sendResult().
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.date_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        int year = mDatePicker.getYear();
                        int month = mDatePicker.getMonth();
                        int day = mDatePicker.getDayOfMonth();

                        Date date = new GregorianCalendar(year, month, day).getTime();
                        sendResult(Activity.RESULT_OK, date);

                    }
                })
                .create();


    }

    //send data back to TaskFragment
    private void sendResult(int resultCode, Date date) {

        if(getTargetFragment() == null) {

            return;
        }
        Intent intent = new Intent();
        //puts date as an extra and call onActivityResults in TaskFragment
        intent.putExtra(EXTRA_DATE,  date);
        getTargetFragment()
                .onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
