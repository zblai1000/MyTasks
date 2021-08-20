package com.bignerdranch.android.mytasks;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class TaskFragment extends Fragment{

    //key for identifying which task to pass into the fragment
    private static final String ARG_TASK_ID = "task_id";

    //key for the value of the date passed from DatePickerFragment to TaskFragment
    private static final String DIALOG_DATE = "DialogDate";

    //key for the value of the date passed from TimePickerFragment to TaskFragment
    private static final String DIALOG_TIME = "DialogTime";

    //request code to have TaskFragment receive date back from DatePickerFragment
    private static final int REQUEST_DATE = 0;

    //request code to have TaskFragment receive contact data back from phone contact's list
    private static final int REQUEST_CONTACT = 1;

    //request code to have TaskFragment receive photo data back from camera
    private static final int REQUEST_PHOTO = 2;

    //request code to have TaskFragment receive time back from TimePickerFragment
    private static final int REQUEST_TIME = 3;

    //variable to store data of current Task
    private Task mTask;

    //variable to host and store input value of title field
    private EditText mTitleField;

    //variable to host and store input value of description field
    private EditText mDescriptionField;

    //variable to host and store input value of date
    private Button mDateButton;

    //variable to host and store state (tick/un-tick) of checkbox
    private CheckBox mDoneCheckBox;

    //variable to set state (true/false) of delete on task
    private Button mDeleteTaskButton;

    //variable to initiate intent to send details of task implicitly
    private Button mReportButton;

    //variable to initiate intent to retrieve contact list on device and assign the person
    //responsible of the task
    private Button mTaskResponsibilityButton;

    //variable to initiate intent to start camera and then retrieve image data if any image taken
    private ImageButton mPhotoButton;

    //variable to display image that has been resized (if any)
    private ImageView mPhotoView;

    //variable to store image path (if any)
    private File mPhotoFile;

    //variable to host and store input value of time
    private Button mTimeButton;

    //variable to set responsibility of a task to null (if any)
    private Button mRemoveResponsibilityButton;

    //variable to store UUID of task retrieved from database
    private UUID taskId;

    //variable to initiate Time object to be used to set the time format
    private Time time = new Time();

    //variable to initiate and store the state of if a person responsible on a task exist
    //0 -> no one is responsible
    //1 -> someone is responsible
    private int responsible = 0;




    //functionality of this method:
        //1. create instance of fragment
        //2. bundle up the fragment arguments
        //3. set fragment arguments
    public static TaskFragment newInstance(UUID taskId){

        Bundle args = new Bundle();
        args.putSerializable(ARG_TASK_ID, taskId);

        TaskFragment fragment = new TaskFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //access the arguments from the bundle upon the creation of this fragment
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);

        taskId = (UUID) getArguments().getSerializable(ARG_TASK_ID);
        mTask = TaskLab.get(getActivity()).getTask(taskId);
        mPhotoFile = TaskLab.get(getActivity()).getPhotoFile(mTask);

    }



    //update fragment when the activity is paused
    //updateTask() is a method from TaskLab where it reads and writes on the database
    //and sends back the updated data
    @Override
    public void onPause(){

        super.onPause();
        TaskLab.get(getActivity()).updateTask(mTask);
    }

    //create view with view elements initiated
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        //inflate a new view from fragment_task
        View v = inflater.inflate(R.layout.fragment_task, container, false);

        //link with task_title view
        mTitleField = (EditText) v.findViewById(R.id.task_title);
        //set value of title (if any)
        mTitleField.setText(mTask.getTitle());

        //handle where there is change in text
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                //set description of task to the inserted value in description field
                    //NOTE:
                        // null value will be checked in Tasklab
                        //if title is null then the task will be deleted upon returning to
                        //task list
                mTask.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //link with task_description view
        mDescriptionField = (EditText) v.findViewById(R.id.task_description);
        //set value of description (if any)
        mDescriptionField.setText(mTask.getDescription());
        //handle when there is change in text
        mDescriptionField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(
                    CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(
                    CharSequence s, int start, int before, int count) {
                //set description of task to the inserted value in description field
                //NOTE:
                    //no checking of null value required as it is optional for a task to
                    //have a description
                mTask.setDescription(s.toString());

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //link with task_time
        mTimeButton = (Button) v.findViewById(R.id.task_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener(){

            //display a TimePickerFragment when the time button is pressed
            @Override
            public void onClick(View v){

                FragmentManager manager = getFragmentManager();
                TimePickerFragment dialog = TimePickerFragment.newInstance(mTask.getDate());
                dialog.setTargetFragment(TaskFragment.this, REQUEST_TIME);
                dialog.show(manager, DIALOG_TIME);
            }
        });

        //link with task_date
        mDateButton = (Button) v.findViewById(R.id.task_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {

            //display a DatePickerFragment when the date button is pressed
            @Override
            public void onClick(View v){

                FragmentManager manager = getFragmentManager();
                DatePickerFragment dialog = DatePickerFragment.newInstance(mTask.getDate());
                dialog.setTargetFragment(TaskFragment.this, REQUEST_DATE);
                dialog.show(manager, DIALOG_DATE);
            }
        });

        //link with task_done
        mDoneCheckBox = (CheckBox)v.findViewById(R.id.task_done);
        //tick the checkbox if the task selected is done
        mDoneCheckBox.setChecked(mTask.isDone() == 1);
        //tick the checkbox if user clicked on it
        mDoneCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked){

                //setDone(1) -> task done
                //setDone(0) -> task not done
                if(isChecked){

                    mTask.setDone(1);
                }else{

                    mTask.setDone(0);
                }

            }
        });


        //link with delete_task
        mDeleteTaskButton = (Button) v.findViewById(R.id.delete_task);
        //delete task the fragment is holding when user click on delete button
        mDeleteTaskButton.setOnClickListener(new View.OnClickListener(){

            //after TaskFragment activity has ended,
            //all tasks are retrieved from the database and passed to TaskListFragment
            //TaskLab will check the state of remove (0, 1):
                //if remove = false -> 0
                //if remove = true -> 1
            //TaskLab will then delete the rows where remove = 1
            @Override
            public void onClick(View v){

                //remove will be set to true and update the database
                mTask.setRemove(true);
                mDeleteTaskButton.setEnabled(false);
                //will end TaskFragment activity to go back to TaskLiskFragment to refresh list
                //to reflect the deletion of the task
                getActivity().finish();
            }
        });

        //link with task_report
        mReportButton = (Button) v.findViewById(R.id.task_report);
        mReportButton.setOnClickListener(new View.OnClickListener(){

            //create implicit intent to send task data to other applications/functions compatible on device
                //usually: SMS, email, copy to clipboard etc.
            public void onClick(View v){

                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getTaskReport());
                i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.task_report_subject));
                i = Intent.createChooser(i, getString(R.string.send_report));
                startActivity(i);
            }
        });

        //link with remove_responsibility
        mRemoveResponsibilityButton = (Button) v.findViewById(R.id.remove_responsibility);
        mRemoveResponsibilityButton.setOnClickListener(new View.OnClickListener(){

            //when clicked, set responsibility of task to null and
            // update the button text to reflect the change
            public void onClick(View v){

                mTask.setResponsible(null);
                mTaskResponsibilityButton.setText(R.string.task_responsibility_text);
                //disable the button that removes the person responsible for the task
                mRemoveResponsibilityButton.setEnabled(false);

            }
        });


        //declare implicit intent to retrive contacts data from device
        final Intent pickContact = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);

        //link with task_responsibility
        mTaskResponsibilityButton = (Button) v.findViewById(R.id.task_responsibility);
        mTaskResponsibilityButton.setOnClickListener(new View.OnClickListener(){

            //when clicked, start the implicit intent to view the contacts list
            //retrieve the contact name selected (if any)
            public void onClick(View v){

                startActivityForResult(pickContact, REQUEST_CONTACT);
            }
        });

        //set the person responsible for the task if there is a person responsible.
        if(mTask.getResponsible() != null){

            mTaskResponsibilityButton.setText(mTask.getResponsible());
            //enable the button that removes the person responsible for the task
            mRemoveResponsibilityButton.setEnabled(true);
        }

        //check if there is a contact app on the device.
        //if there is no contact app, disable the button that sets the responsibility.
        PackageManager packageManager = getActivity().getPackageManager();
        if (packageManager.resolveActivity(pickContact, PackageManager.MATCH_DEFAULT_ONLY) == null){

            mTaskResponsibilityButton.setEnabled(false);
        }


        //link with task_camera
        mPhotoButton = (ImageButton) v.findViewById(R.id.task_camera);
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        //check if there is a camera app available and a location to save the photo.
        //if none, then disable the photo button.
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        mPhotoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v){

                //gives permission to access Android file provider service.
                Uri uri = FileProvider.getUriForFile(getActivity(),
                        "com.bignerdranch.android.mytasks.fileprovider", mPhotoFile);

                //allows implicit intent to capture image wiht media store extra output.
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);

                List<ResolveInfo> cameraActivities = getActivity().getPackageManager()
                        .queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY);

                for (ResolveInfo activity: cameraActivities){

                    //grant permission to access cameras and write data file.
                    getActivity().grantUriPermission(activity.activityInfo.packageName, uri,
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                }

                //start implicit intent
                //expecting a return to receive the captured image.
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });


        //link with task_photo
        mPhotoView = (ImageView) v.findViewById(R.id.task_photo);
        //format the image and put the image in the photo view.
        updatePhotoView();

        //retrieve saved instances of variables upon rotation.
        if(savedInstanceState != null){

            mTask.setRemove(savedInstanceState.getBoolean("remove", true));
            String tempUUID_asString = savedInstanceState.getString("UUID");
            taskId = UUID.fromString(tempUUID_asString);
            responsible = savedInstanceState.getInt("responsible");



        }

        //enables and disables the mRemoveResponsibilityButton
        manageRemoveResponsibilityButton();

        return v;
    }



    //enables and disables the mRemoveResponsibilityButton based on if the task has already
    //been assigned to a person responsible or not
        //person assigned -> responsible set to 1
        //person not assigned -> responsible set to 0
        //after remove person -> responsible set to 0
    private void manageRemoveResponsibilityButton(){

        if (responsible == 1){

            mRemoveResponsibilityButton.setEnabled(true);

        } else if (responsible == 0){

            mRemoveResponsibilityButton.setEnabled(false);

        }
    }

    //to retrieve the EXTRA value
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        //if user did not click on "ok" when selecting date or time, return nothing
        if (resultCode != Activity.RESULT_OK){

            return;
        }

        //set the date of the task
        if (requestCode == REQUEST_DATE){

            //initiate date object and retrieve value of date from DatePickerFragment
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            mTask.setDate(date);
            //update mDateButton text and the time
            updateDate();
        }

        //set the person responsible for the task
        else if(requestCode == REQUEST_CONTACT && data != null){

            Uri contactUri = data.getData();

            //Specify which fields to query to return values for
            String[] queryFields = new String[]{

                    ContactsContract.Contacts.DISPLAY_NAME
            };

            //Perform the query
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields,
                    null, null, null);

            try{

                //Double-check that you actually got results
                if(c.getCount() == 0){

                    responsible = 0;
                    manageRemoveResponsibilityButton();

                    return;
                }

                //Pull out the first column of the first row of data - that is your suspect's name
                c.moveToFirst();
                String tempResponsible = c.getString(0);
                mTask.setResponsible(tempResponsible);
                mTaskResponsibilityButton.setText(tempResponsible);
                //set responsible to 1, which will determine if the remove person responsible button
                //is enabled or disabled.
                responsible = 1;
                //to determine if the remove person responsible button is enabled or disabled/
                    //will be disabled if no person is selected (responsible = 0)
                    //will be enabled if someone is selected (responsible = 1)
                manageRemoveResponsibilityButton();

            } finally{

                c.close();
            }
        }
        //set the image captured as the image for the task
        else if (requestCode == REQUEST_PHOTO){

            Uri uri = FileProvider.getUriForFile(getActivity(),
                    "com.bignerdranch.android.mytasks.fileprovider",
                    mPhotoFile);

            getActivity().revokeUriPermission(uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            //will update the photo view if user have captured an image
            updatePhotoView();
        }
        //set the time chosen by users
        else if (requestCode == REQUEST_TIME) {

            //initiate date object and retrieve value of time from DatePickerFragment
            Date time = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            mTask.setDate(time);
            //update mTimeButton text
            updateTime();

        }
    }

    private void updateDate() {


        mDateButton.setText(String.format("%1$td %1$tb %1$tY", mTask.getDate()));
        //the time will be updated when the user select a date
       updateTime();
    }

    private void updateTime() {

        //set the time to the value of the time in updateDate()
        //the time variable in this method will only be used for displaying purposes
        time.set(mTask.getDate().getTime());
        String timeFormat = time.format("%I:%M %p");
        mTimeButton.setText(timeFormat);

    }

    private String getTaskReport(){

        //variable to store message on task done or not
        String doneString = null;

        //isDone == 1 -> complete
        //is done == 0 (else) -> incomplete
        if(mTask.isDone() == 1){

            doneString = getString(R.string.task_report_complete);
        } else{

            doneString = getString(R.string.task_report_incomplete);
        }

        //set date format to be sent.
        String dateFormat = "EEE, MMM dd";
        String dateString = DateFormat.format(dateFormat, mTask.getDate()).toString();

        //variable to store person responsible (if any).
        String responsible = mTask.getResponsible();
        if(responsible == null){

            responsible = getString(R.string.task_report_no_responsibility);
        }else{

            responsible = getString(R.string.task_report_responsibility, responsible);
        }

        String report = getString(R.string.task_report, mTask.getTitle(), dateString,
                doneString, responsible);

        return report;
    }

    //
    private void updatePhotoView(){

        //format photo to be displayed on photoView (if any)
        if(mPhotoFile == null || !mPhotoFile.exists()){

            mPhotoView.setImageDrawable(null);
        } else{

            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());

            mPhotoView.setImageBitmap(bitmap);
        }
    }

    //save the state of the variables when rotating screen.
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState){

        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("remove", mTask.getRemove());
        savedInstanceState.putString("UUID", taskId.toString());
        savedInstanceState.putInt("responsible", responsible);
    }















}
