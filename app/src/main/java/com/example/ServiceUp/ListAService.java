package com.example.ServiceUp;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ListAService extends Fragment {

    private EditText profession;
    private EditText description;
    private EditText rate;
    private Button startTimePicker;
    private Button endTimePicker;
    private Button save;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;
    private FirebaseDatabase mDatabase;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String startTime = "";
    private String endTime = "";

    private Date date = null;
    private Chip monday;
    private Chip tuesday;
    private Chip wednesday;
    private Chip thursday;
    private Chip friday;
    private Chip saturday;
    private Chip sunday;
    private Boolean startOrEnd = false;
    private int pickedStartHour;
    private int pickedStartMinute;
    private int pickedEndHour;
    private int pickedEndMinute;
    private HashMap<String, Boolean> availability = new HashMap<>();
    ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_list_a_service, container, false);

        profession = view.findViewById(R.id.listProfession);
        description = view.findViewById(R.id.listDescription);
        rate = view.findViewById(R.id.listRate);
        startTimePicker = view.findViewById(R.id.startTimePicker);
        endTimePicker = view.findViewById(R.id.endTimePicker);
        save = view.findViewById(R.id.listAServiceSave);
        monday = view.findViewById(R.id.monday);
        tuesday = view.findViewById(R.id.tuesday);
        wednesday = view.findViewById(R.id.wednesday);
        thursday = view.findViewById(R.id.thursday);
        friday = view.findViewById(R.id.friday);
        saturday = view.findViewById(R.id.saturday);
        sunday = view.findViewById(R.id.sunday);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");

        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference

        mDatabaseReference.keepSynced(true);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Saving to our database
                showBasicResDialog();

            }
        });

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                if (!dataSnapshot.child("startTime").getValue().toString().equals("")) {

                    startOrEnd = true;
                    startTime = dataSnapshot.child("startTime").getValue().toString();
                    parseTime(startTime);

                }
                if (!dataSnapshot.child("endTime").getValue().toString().equals("")) {

                    startOrEnd = false;
                    endTime = dataSnapshot.child("endTime").getValue().toString();
                    parseTime(endTime);
                }

                profession.setText(dataSnapshot.child("profession").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("profession").getValue().toString().substring(1));
                if (!dataSnapshot.child("jobDescription").getValue().toString().equals("")) {
                    description.setText(dataSnapshot.child("jobDescription").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("jobDescription").getValue().toString().substring(1));

                }


                rate.setText(dataSnapshot.child("rate").getValue() == Double.valueOf(0) ? "" : dataSnapshot.child("rate").getValue().toString());
//                rate.setFilters(new InputFilter[] {new InputFilter(5, 2) {
//                    @Override
//                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
//                        return null;
//                    }
//                }});

                availability = (HashMap<String, Boolean>) dataSnapshot.child("availability").getValue();

                monday.setChecked(availability.get("monday"));
                tuesday.setChecked(availability.get("tuesday"));
                wednesday.setChecked(availability.get("wednesday"));
                thursday.setChecked(availability.get("thursday"));
                friday.setChecked(availability.get("friday"));
                saturday.setChecked(availability.get("saturday"));
                sunday.setChecked(availability.get("sunday"));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        startTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

                try {
                    date = sdf.parse(startTime);
                    c.setTime(date);
                } catch (
                        ParseException e) {
                }

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, false);
                timePickerDialog.show();

                startOrEnd = true;


            }
        });

        endTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

                try {
                    date = sdf.parse(endTime);
                    c.setTime(date);
                } catch (
                        ParseException e) {
                }

                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), timePickerListener, hour, minute, false);
                timePickerDialog.show();

                startOrEnd = false;

            }
        });

        return view;

    }


    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar c = Calendar.getInstance();

            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

//            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
//

//                    currentTimeString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

            String currentTimeString = "";

            if (hourOfDay > 12) {
                currentTimeString = (minute < 10) ? hourOfDay - 12 + ":0" + minute + " PM" : hourOfDay - 12 + ":" + minute + " PM";
            } else if (hourOfDay == 12) {
                currentTimeString = (minute < 10) ? hourOfDay + ":0" + minute + " PM" : hourOfDay + ":" + minute + " PM";
            } else {

                currentTimeString = (minute < 10) ? hourOfDay + ":0" + minute + " AM" : hourOfDay + ":" + minute + " AM";
            }

            if (startOrEnd) {
                pickedStartHour = hourOfDay;
                pickedStartMinute = minute;

                startTimePicker.setText(currentTimeString);
                startTime = hourOfDay + ":" + minute;
            } else {
                pickedEndHour = hourOfDay;
                pickedEndMinute = minute;

                endTimePicker.setText(currentTimeString);
                endTime = hourOfDay + ":" + minute;
            }

        }


    };


    private void parseTime(String time) {
        Date date;
        int hour;
        int minute;

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

        try {
            date = sdf.parse(time);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);

            String myTime = "";

            if (hour > 12) {
                myTime = (minute < 10) ? hour - 12 + ":0" + minute + " PM" : hour - 12 + ":" + minute + " PM";
            } else if (hour == 12) {
                myTime = (minute < 10) ? hour + ":0" + minute + " PM" : hour + ":" + minute + " PM";
            } else {
                myTime = (minute < 10) ? hour + ":0" + minute + " AM" : hour + ":" + minute + " AM";
            }

            if (startOrEnd) {
                pickedStartHour = hour;
                pickedStartMinute = minute;
                startTimePicker.setText(myTime);
            } else {
                pickedEndHour = hour;
                pickedEndMinute = minute;
                endTimePicker.setText(myTime);
            }

        } catch (
                ParseException e) {
        }

    }


    //Handling Dialog message before saving changes
    private void showBasicResDialog() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Save Changes")
                .setMessage("Do you want to save your changes?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dayAndTimeValidation();

                    }
                })
                .create().show();

    }

    private void notAvailable() {


        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("You are not available!!!")
                .setMessage("Please select a day")
                .create().show();

    }

    private void noJobListed() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Input missing!!!")
                .setMessage("You do not have a job listed")
                .create().show();

    }

    private void invalidTime() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Invalid time!!!")
                .setMessage("Check the start and end time. And also make sure you have at least 30 minute of availability")
                .create().show();

    }

    private void invalidRate() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Invalid rate!!!")
                .setMessage("Check the rate")
                .create().show();

    }

    private void dayAndTimeValidation() {


        if (!TextUtils.isEmpty(profession.getText().toString().trim())) {
            Boolean available = false;

            availability.put("monday", monday.isChecked());
            availability.put("tuesday", tuesday.isChecked());
            availability.put("wednesday", wednesday.isChecked());
            availability.put("thursday", thursday.isChecked());
            availability.put("friday", friday.isChecked());
            availability.put("saturday", saturday.isChecked());
            availability.put("sunday", sunday.isChecked());

            for (HashMap.Entry it : availability.entrySet()) {
                boolean key = (Boolean) it.getValue();

                if (key) {
                    available = true;
                }

            }


            if (available) {
                // checkTime

                if (rate.getText().toString().isEmpty() || Double.parseDouble(rate.getText().toString()) <= 0) {
                    invalidRate();
                } else if (pickedStartHour < pickedEndHour) {
                    startSaving();

                } else if ((pickedStartHour == pickedEndHour) && ((pickedEndHour * 60 + pickedEndMinute) - (pickedStartHour * 60 + pickedStartMinute)) >= 30) {

                    startSaving();

                } else {
                    invalidTime();
                }


            } else {
                notAvailable();
            }

        } else {
            noJobListed();
        }

    }

    private void startSaving() {

        final String professionStr = profession.getText().toString().trim();
        final String jobDescriptionStr = description.getText().toString().trim();


        currentUserDb.child("profession").setValue(professionStr.toLowerCase());
        currentUserDb.child("jobDescription").setValue(jobDescriptionStr.toLowerCase());
        currentUserDb.child("rate").setValue(Double.valueOf(rate.getText().toString()));

        currentUserDb.child("startTime").setValue(startTime.toString());
        currentUserDb.child("endTime").setValue(endTime.toString());
        currentUserDb.child("availability").child("monday").setValue(monday.isChecked());
        currentUserDb.child("availability").child("tuesday").setValue(tuesday.isChecked());
        currentUserDb.child("availability").child("wednesday").setValue(wednesday.isChecked());
        currentUserDb.child("availability").child("thursday").setValue(thursday.isChecked());
        currentUserDb.child("availability").child("friday").setValue(friday.isChecked());
        currentUserDb.child("availability").child("saturday").setValue(saturday.isChecked());
        currentUserDb.child("availability").child("sunday").setValue(sunday.isChecked());


        Intent intent = new Intent(getActivity(), CentralContainer.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);


    }
}


