package com.example.ServiceUp;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

public class addToCart extends Fragment {

    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;
    private DatabaseReference providerDb;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseWork;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private User providerSelected;
    private ImageView providerPic;
    private TextView availabilityText;
    private TextView serviceProviderName;
    private TextView jobTitle;
    private TextView jobDescription;
    private TextView rating;
    private TextView cartCurrency;
    private TextView rate;
    private TextView cartTime;
    private MaterialTextView providerAvailability;
    private MaterialTextView providerAvailabilityTime;
    private LinearLayout addToCartWrapper;
    private int providerStartHour;
    private int providerStartMinute;
    private int providerEndHour;
    private int providerEndMinute;
    private int pickedStartHour;
    private int pickedStartMinute;
    private int pickedEndHour;
    private int pickedEndMinute;
    private EditText address;
    private EditText instruction;
    private MaterialButton bookService;
    private Button cartStartTimePicker;
    private Button cartEndTimePicker;
    private Button cartDatePicker;
    private Date date = null;
    private Toolbar toolbar;
    private String availabilityDayStr = "";
    private String availabilityTimeStr = "";
    private StringBuilder providerStartTimeBlr = new StringBuilder("");
    private StringBuilder providerEndTimeBlr = new StringBuilder("");
    private String startTimePicked = "";
    private String endTimePicked = "";
    private String datePicked = "";
    private Boolean startOrEnd = false;
    private String currentUserId;
    private String addressStr;
    private String currentUserFirstName;
    private String currentUserLastName;
    private HashMap<String, String> myProviders = new HashMap<>();
    private List<String> dayList = new ArrayList<>();
    private String dayPickedStr = "";
    private double workTotal;

    public addToCart(User providerSelected, Toolbar toolbar) {
        this.providerSelected = providerSelected;
        this.toolbar = toolbar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_add_to_cart, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference
        providerDb = mDatabaseReference.child(providerSelected.getUserId());

        mDatabaseWork = FirebaseDatabase.getInstance().getReference().child("MWorks");

        mDatabaseReference.keepSynced(true);

        providerPic = view.findViewById(R.id.cartproviderPic);
        availabilityText = view.findViewById(R.id.availabilityText);
        serviceProviderName = view.findViewById(R.id.cartProviderName);
        jobTitle = view.findViewById(R.id.cartJobTitle);
        jobDescription = view.findViewById(R.id.cartJobDescription);
        rating = view.findViewById(R.id.cartRating);

        cartCurrency = view.findViewById(R.id.cartCurrency);
        rate = view.findViewById(R.id.cartRate);
        cartTime = view.findViewById(R.id.cartTime);
        address = view.findViewById(R.id.cartAddress);
        providerAvailability = view.findViewById(R.id.providerAvailability);
        providerAvailabilityTime = view.findViewById(R.id.providerAvailabilityTime);
        cartStartTimePicker = view.findViewById(R.id.cartStartTimePicker);
        cartEndTimePicker = view.findViewById(R.id.cartEndTimePicker);
        cartDatePicker = view.findViewById(R.id.cartDatePicker);
        instruction = view.findViewById(R.id.cartInstruction);
        bookService = view.findViewById(R.id.bookService);
        addToCartWrapper = view.findViewById(R.id.addToCartWrapper);

        toolbar.getMenu().clear();
        toolbar.setTitle("Appointment");

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                currentUserId = dataSnapshot.child("userId").getValue().toString();
                currentUserFirstName = dataSnapshot.child("firstName").getValue().toString();
                currentUserLastName = dataSnapshot.child("lastName").getValue().toString();
                addressStr = dataSnapshot.child("address").getValue().toString() + " " +
                        dataSnapshot.child("city").getValue().toString() + " " +
                        dataSnapshot.child("state").getValue().toString() + " " +
                        dataSnapshot.child("zipCode").getValue().toString();
                if (dataSnapshot.child("myProviders").getValue() != null) {
                    myProviders = (HashMap<String, String>) dataSnapshot.child("myProviders").getValue();

                }

                address.setText(addressStr);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        if (!providerSelected.getImage().equals("")) {
            Picasso.get().load(providerSelected.getImage()).transform(new RoundedCornersTransformation(40, 40)).into(providerPic);
        }
        serviceProviderName.setText(providerSelected.getFirstName().substring(0, 1).toUpperCase() + providerSelected.getFirstName().substring(1) + " " + providerSelected.getLastName().substring(0, 1).toUpperCase() + providerSelected.getLastName().substring(1));
        jobTitle.setText(providerSelected.getProfession().isEmpty() ? "" : providerSelected.getProfession().substring(0, 1).toUpperCase() + providerSelected.getProfession().substring(1));

        if (!providerSelected.getJobDescription().equals("")) {

            jobDescription.setText(providerSelected.getJobDescription().substring(0, 1).toUpperCase() + providerSelected.getJobDescription().substring(1));
        }

        if (!providerSelected.getRating().isEmpty()) {
            rating.setText(providerSelected.getRating());
        }


        if (!providerSelected.getStartTime().equals("")) {
            startOrEnd = true;
            providerStartTimeBlr.delete(0, providerStartTimeBlr.length());
            parseTime(providerSelected.getStartTime(), providerStartTimeBlr);
        }
        if (!providerSelected.getEndTime().equals("")) {
            startOrEnd = false;
            providerEndTimeBlr.delete(0, providerEndTimeBlr.length());
            parseTime(providerSelected.getEndTime(), providerEndTimeBlr);
        }


        for (HashMap.Entry it : providerSelected.getAvailability().entrySet()) {
            boolean key = (Boolean) it.getValue();

            if (key) {
//                String nextDay = String.format("%-30s %20s -- %s \n\n", it.getKey(), providerStartTime.toString(), providerEndTime.toString());
//                availabilityStr += nextDay;
                availabilityDayStr += (it.getKey() + "\n\n");
                dayList.add((String) it.getKey());
                availabilityTimeStr += (providerStartTimeBlr.toString() + " -- " + providerEndTimeBlr.toString() + "\n\n");

            }

        }

        if (dayList.isEmpty()) {
            rating.setVisibility(View.GONE);
            availabilityText.setText("This provider is not available");
            availabilityText.setTextColor(Color.RED);
            cartCurrency.setVisibility(View.GONE);
            rate.setVisibility(View.GONE);
            cartTime.setVisibility(View.GONE);
            addToCartWrapper.setVisibility(View.GONE);


        } else {
            rate.setText(String.valueOf(providerSelected.getRate()));
        }

        providerAvailability.setText(availabilityDayStr);
        providerAvailabilityTime.setText(availabilityTimeStr);


        cartDatePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM dd, yyyy");

                try {
                    date = sdf.parse(datePicked);
                    c.setTime(date);
                } catch (
                        ParseException e) {
                }

                int mYear = c.get(Calendar.YEAR);
                int mMonth = c.get(Calendar.MONTH);
                int mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog dateDialog = new DatePickerDialog(getActivity(), datePickerListener, mYear, mMonth, mDay);
                dateDialog.show();

            }
        });

        // HANDLING THE TIME PICKER BUTTONS
        cartStartTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

                try {
                    date = sdf.parse(startTimePicked);
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

        cartEndTimePicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");

                try {
                    date = sdf.parse(endTimePicked);
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

        // GETTING THE MOST UPDATED DATA FROM THE DATABASE

        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);

                if (user.getUserId().equals(providerSelected.getUserId())) {

                    providerSelected = user;

                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);

                if (user.getUserId().equals(providerSelected.getUserId())) {

                    providerSelected = user;

                    //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new addToCart(providerSelected, toolbar)).commit();
                    // getActivity().finish();

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {


            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        bookService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dayAndTimeValidation();
                //showBasicResDialog();
            }
        });


        return view;
    }

    private void parseTime(String time, StringBuilder timeBuilder) {
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

            if (startOrEnd) {
                providerStartHour = hour;
                providerStartMinute = minute;

            } else {
                providerEndHour = hour;
                providerEndMinute = minute;
            }

            if (hour > 12) {
                timeBuilder.append((minute < 10) ? hour - 12 + ":0" + minute + " PM" : hour - 12 + ":" + minute + " PM");
            } else if (hour == 12) {
                timeBuilder.append((minute < 10) ? hour + ":0" + minute + " PM" : hour + ":" + minute + " PM");
            } else {
                timeBuilder.append((minute < 10) ? hour + ":0" + minute + " AM" : hour + ":" + minute + " AM");
            }

        } catch (
                ParseException e) {
        }

    }

    private TimePickerDialog.OnTimeSetListener timePickerListener = new TimePickerDialog.OnTimeSetListener() {

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            Calendar c = Calendar.getInstance();

            c.set(Calendar.HOUR_OF_DAY, hourOfDay);
            c.set(Calendar.MINUTE, minute);

            String currentTimeString;


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

                cartStartTimePicker.setText(currentTimeString);
                startTimePicked = hourOfDay + ":" + minute;
            } else {
                pickedEndHour = hourOfDay;
                pickedEndMinute = minute;

                cartEndTimePicker.setText(currentTimeString);
                endTimePicked = hourOfDay + ":" + minute;
            }

        }


    };


    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            int weekDay = c.get(Calendar.DAY_OF_WEEK);

            switch (weekDay) {
                case Calendar.SUNDAY:
                    dayPickedStr = "sunday";
                    break;
                case Calendar.MONDAY:
                    dayPickedStr = "monday";
                    break;
                case Calendar.TUESDAY:
                    dayPickedStr = "tuesday";
                    break;
                case Calendar.WEDNESDAY:
                    dayPickedStr = "wednesday";
                    break;
                case Calendar.THURSDAY:
                    dayPickedStr = "thursday";
                    break;
                case Calendar.FRIDAY:
                    dayPickedStr = "friday";
                    break;
                case Calendar.SATURDAY:
                    dayPickedStr = "saturday";
                    break;
            }

            // String currentDateString = DateFormat.getDateInstance(DateFormat.DEFAULT).format(c.getTime());

            String currentDateString  = new SimpleDateFormat("EEE, MMM dd, YYYY").format(c.getTime());

            cartDatePicker.setText(currentDateString);
            datePicked = currentDateString;
        }
    };

    //Handling Dialog message before saving changes
    private void confirmation() {

        workTotal = (Double.parseDouble(rate.getText().toString()) / 60) * ((pickedEndHour * 60 + pickedEndMinute) - (pickedStartHour * 60 + pickedStartMinute));

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Booking Service")
                .setMessage("Your total is" + " $ " + String.format("%.2f", workTotal)
                        + "\n\nAll payment is done in cash directly to the provider"
                        + "\n\nDo you want to complete this transaction?")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //dayAndTimeValidation();
                        startSaving();
                    }
                })
                .create().show();

    }

    private void invalidDay() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Invalid Day !!!")
                .setMessage("Check the provider's availability")
                .create().show();

    }

    private void invalidTime() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Invalid Time!!!")
                .setMessage("Check the provider's availability")
                .create().show();

    }

    private void notAvailable() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Provider Not Available!!!")
                .setMessage("You may choose another provider")
                .create().show();

    }

    private void insufficientTime() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Insufficient Time")
                .setMessage("The appointment time should be 30 minutes or more")
                .create().show();

    }


    private void requiredFields() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Input Missing!!!")
                .setMessage("You must fill in all required fields")
                .create().show();

    }

    private void dayAndTimeValidation() {

        addressStr = address.getText().toString().trim();

        if(TextUtils.isEmpty(addressStr))
        {
            requiredFields();
        }

        else if (dayList.contains(dayPickedStr)) {

            if (((pickedStartHour >= providerStartHour && pickedEndHour <= providerEndHour) && (pickedStartHour <= pickedEndHour)) || ((pickedStartHour == providerStartHour && pickedStartMinute >= providerStartMinute) && (pickedEndHour == providerEndHour && pickedEndMinute <= providerEndMinute))) {
                if (((pickedEndHour * 60 + pickedEndMinute) - (pickedStartHour * 60 + pickedStartMinute)) >= 30) {

                    confirmation();
                    //startSaving();
                } else {
                    insufficientTime();
                }
            } else {
                System.out.println(pickedStartHour + ">" + providerStartHour + "\t" + pickedEndHour + "<" + providerEndHour);
                invalidTime();
            }

        } else if (dayList.isEmpty()) {
            notAvailable();
        } else {
            invalidDay();
        }


    }

    private void startSaving() {


        String instructionStr = "";
        if (!instruction.getText().toString().equals("Add a comment")) {
            instructionStr = instruction.getText().toString().trim();
        }

        if (!TextUtils.isEmpty(datePicked) && !TextUtils.isEmpty(startTimePicked) && !TextUtils.isEmpty(endTimePicked)) {


            myProviders.put(providerSelected.getUserId(), providerSelected.getFirstName() + " " + providerSelected.getLastName());
            currentUserDb.child("myProviders").setValue(myProviders);


            HashMap<String, String> myCustomers = new HashMap<>();
            if (providerSelected.getMyCustomers() != null) {
                myProviders = providerSelected.getMyCustomers();
            }
            myCustomers.put(currentUserId, currentUserFirstName + " " + currentUserLastName);

            providerDb.child("myCustomers").setValue(myCustomers);


            DatabaseReference currentWork = mDatabaseWork.push();
            HashMap<String, String> dataToSave = new HashMap<>();


            dataToSave.put("orderId", currentWork.getKey());
            dataToSave.put("customerId", currentUserId);
            dataToSave.put("providerId", providerSelected.getUserId());
            dataToSave.put("customerFirstName", currentUserFirstName);
            dataToSave.put("customerLastName", currentUserLastName);
            dataToSave.put("providerFirstName", providerSelected.getFirstName());
            dataToSave.put("providerLastName", providerSelected.getLastName());
            dataToSave.put("startTime", startTimePicked);
            dataToSave.put("endTime", endTimePicked);
            dataToSave.put("workDate", datePicked);
            dataToSave.put("workTotal", String.format("%.2f", workTotal));
            dataToSave.put("address", addressStr);
            dataToSave.put("instruction", instructionStr.toLowerCase());
            dataToSave.put("profession", providerSelected.getProfession());
            dataToSave.put("transactionDate", java.text.DateFormat.getDateTimeInstance().format(new Date(Long.valueOf(System.currentTimeMillis()))));
            dataToSave.put("cancellerId", "");
            dataToSave.put("cancellationDate", "");

            currentWork.setValue(dataToSave);

            // SHOULD BE BELOW OTHERWISE IT DOES NOT GET UPLOADED
            currentWork.child("rating").setValue("0");
            currentWork.child("status").setValue(1);  // status  =======> 0 cancelled, 1 on, 2 complete, 3 complete and reviewed

            Intent intent = new Intent(getActivity(), CentralContainer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();
        } else {
            requiredFields();
        }


    }


}
