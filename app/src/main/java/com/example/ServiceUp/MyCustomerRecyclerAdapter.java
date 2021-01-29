package com.example.ServiceUp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MyCustomerRecyclerAdapter extends RecyclerView.Adapter<MyCustomerRecyclerAdapter.ViewHolder> {


    private Context context;
    private List<Work> workList;
    private List<Work> workFullList;
    private List<User> userList;
    public Toolbar toolbar;

    private Boolean startOrEnd;
    private String startTime;
    private String endTime;
    private StringBuilder timeBuilder = new StringBuilder("");
    private String phoneNumber;
    private String email;
    private String customerFirstName;

    private DatabaseReference mDatabaseWork;
    private DatabaseReference currentWork;


    private static final int REQUEST_CALL = 1;

    public MyCustomerRecyclerAdapter(Context context, List<Work> workList, List<User> userList, Toolbar toolbar) {
        this.context = context;
        this.workList = workList;
        this.workFullList = workList;
        this.userList = userList;
        this.toolbar = toolbar;
        this.mDatabaseWork = FirebaseDatabase.getInstance().getReference().child("MWorks");
    }

    @NonNull
    @Override
    public MyCustomerRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_customer_row, parent, false);

        ViewHolder evh = new ViewHolder(view, context);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyCustomerRecyclerAdapter.ViewHolder holder, int position) {


        final Work thisWork = workList.get(position);


        String firstName = "";
        String lastName = "";


        for (int i = 0; i < userList.size(); i++) {
            if (thisWork.getCustomerId().equals(userList.get(i).getUserId())) {
                firstName = userList.get(i).getFirstName().substring(0, 1).toUpperCase() + userList.get(i).getFirstName().substring(1);
                lastName = userList.get(i).getLastName().substring(0, 1).toUpperCase() + userList.get(i).getLastName().substring(1);
                holder.myCustomerName.setText(firstName + " " + lastName);

                if (!userList.get(i).getImage().equals("")) {
                    Picasso.get().load(userList.get(i).getImage()).transform(new CropCircleTransformation()).into(holder.myCustomerPic);
                    //Picasso.get().load(imageUrl).transform(new RoundedCornersTransformation(40, 40)).into(navPicture);
                }

                phoneNumber = userList.get(i).getPhoneNumber();
                email = userList.get(i).getEmail();
                customerFirstName = firstName;
            }

        }

//        holder.myCustomerProfession.setText(thisWork.getProfession().substring(0, 1).toUpperCase() + thisWork.getProfession().substring(1));
        holder.myCustomerSelectedDate.setText(thisWork.getWorkDate());

        parseTime(thisWork.getStartTime(), timeBuilder);
        holder.myCustomerStartTime.setText(timeBuilder);

        parseTime(thisWork.getEndTime(), timeBuilder);
        holder.myCustomerEndTime.setText(timeBuilder);
        holder.workAddress.setText(thisWork.getAddress());

        holder.myCustomerWorkTotal.setText(thisWork.getWorkTotal());

        if(TextUtils.isEmpty(thisWork.getInstruction()))
        {
            holder.myCustomerInstruction.setVisibility(View.GONE);

        }
        else
        {
            holder.myCustomerInstruction.setText(thisWork.getInstruction().substring(0, 1).toUpperCase() + thisWork.getInstruction().substring(1));
        }

//        holder.myCustomerInstruction.setText(TextUtils.isEmpty(thisWork.getInstruction()) ? "" : thisWork.getInstruction().substring(0, 1).toUpperCase() + thisWork.getInstruction().substring(1));

        holder.myCustomerTransDate.setText(thisWork.getTransactionDate());

        final PopupMenu popupMenu = new PopupMenu(context, holder.popupMenuBtn);
        popupMenu.inflate(R.menu.customer_popup_menu);

        contextMenuHandler(thisWork, popupMenu);

        holder.popupMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                contextMenuHandler(thisWork, popupMenu);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {


                        switch (item.getItemId()) {
                            case R.id.customer_popup_call:
                                callCustomer();
                                break;
                            case R.id.customer_popup_email:
                                emailCustomer();
                                break;

                            case R.id.customer_popup_cancel:

                                confirmCancellation(thisWork);
                                break;
                            default:
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        if (thisWork.getStatus() == 0) {
            holder.myCustomerOrderStatus.setText("Cancelled");
            holder.myCustomerOrderStatus.setTextColor(Color.RED);
        } else if (thisWork.getStatus() == 1) {
            holder.myCustomerOrderStatus.setText("On");
            holder.myCustomerOrderStatus.setTextColor(Color.GREEN);
        }
        if (thisWork.getStatus() >= 2) {
            holder.myCustomerOrderStatus.setText("Complete");
            holder.myCustomerOrderStatus.setTextColor(Color.BLACK);
        }


    }

    private void contextMenuHandler(Work thisWork, PopupMenu popupMenu) {

        if (thisWork.getStatus() == 0 || thisWork.getStatus() >= 2) {
            popupMenu.getMenu().findItem(R.id.customer_popup_cancel).setVisible(false);
        }

        String date1 = thisWork.getWorkDate() + " " + thisWork.getEndTime();
        String date2 = java.text.DateFormat.getDateTimeInstance().format(new Date(Long.valueOf(String.valueOf(java.lang.System.currentTimeMillis()))));

        if (timeComparer(date1, date2) && thisWork.getStatus() < 2) {

            currentWork = mDatabaseWork.child(thisWork.getOrderId());
            currentWork.child("status").setValue(2);

            toolbar.getMenu().clear();
            FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, new MyProviders(toolbar)).commit();
        }

    }



    private Boolean timeComparer(String date1, String date2) {

        SimpleDateFormat d1Format = new SimpleDateFormat("EEEE, MMMM dd, yyyy k:mm");
        SimpleDateFormat d2Format = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss a");

        Date d1 = null;
        Date d2 = null;
        try {
            d1 = d1Format.parse(date1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            d2 = d2Format.parse(date2);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (d1.compareTo(d2) > 0) {

            System.out.println("IS NOT HAPPENED YET");
            return false;
        } else {

            System.out.println("HAS HAPPENED");
            return true;
        }

    }

//    private void requiredFields() {
//
//        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
//                .setTitle("Cancellation Error!!!")
//                .setMessage("This service is already cancelled")
//                .create().show();
//
//    }


    private void confirmCancellation(final Work thisWork) {

//
//        if (thisWork.getStatus() == true) {

        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Cancel Service")
                .setMessage("Do you want to cancel this order?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        currentWork = mDatabaseWork.child(thisWork.getOrderId());


                        currentWork.child("status").setValue(0);
                        currentWork.child("cancellerId").setValue(thisWork.getProviderId());
                        currentWork.child("cancellationDate").setValue(String.valueOf(System.currentTimeMillis()));


                        toolbar.getMenu().clear();
                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.fragment_container, new MyCustomers(toolbar)).commit();
                        // notifyDataSetChanged();
                        Toast.makeText(context, "Order cancelled", Toast.LENGTH_LONG).show();


                    }
                })
                .create().show();


    }


    private void parseTime(String time, StringBuilder timeBuilder) {
        Date date;
        int hour;
        int minute;

        timeBuilder.delete(0, timeBuilder.length());

        SimpleDateFormat sdf = new SimpleDateFormat("k:mm");

        try {
            date = sdf.parse(time);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            hour = c.get(Calendar.HOUR_OF_DAY);
            minute = c.get(Calendar.MINUTE);


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


    private void callCustomer() {
        if (phoneNumber.trim().length() > 0) {

            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context,
                        new String[]{Manifest.permission.CALL_PHONE}, REQUEST_CALL);
            } else {
                String dial = "tel:" + phoneNumber;
                context.startActivity(new Intent(Intent.ACTION_CALL, Uri.parse(dial)));
            }

        } else {
            Toast.makeText(context, "There is no phone number associated to this provider", Toast.LENGTH_SHORT).show();
        }
    }

    private void emailCustomer() {
        String recipientList = email;
        String[] recipients = recipientList.split(",");

        String subject = "Info Request";
        String message = "Hello " + customerFirstName + ",\n\n";

        Intent intent = new Intent(Intent.ACTION_SEND); // ACTION_SENDTO

        intent.putExtra(Intent.EXTRA_EMAIL, recipients);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);


        intent.setType("application/octet-stream");
        intent.setType("message/rfc822");
        context.startActivity(Intent.createChooser(intent, "Choose an email client"));
    }


    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return workList.size();
    }

    public void filterList(List<Work> filteredList) {
        workList = filteredList;
        notifyDataSetChanged();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView popupMenuBtn;
        public ImageView myCustomerPic;
        public TextView myCustomerName;
        public TextView myCustomerSelectedDate;
        public TextView myCustomerStartTime;
        public TextView myCustomerEndTime;
        public TextView workAddress;
        public TextView myCustomerWorkTotal;
        public TextView myCustomerInstruction;
        public TextView myCustomerTransDate;
        public TextView myCustomerOrderStatus;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);

            popupMenuBtn = itemView.findViewById(R.id.myCustomerPopupMenuBtn);
            myCustomerPic = itemView.findViewById(R.id.myCustomerPic);
            myCustomerName = itemView.findViewById(R.id.myCustomerName);
            myCustomerSelectedDate = itemView.findViewById(R.id.myCustomerSelectedDate);
            myCustomerStartTime = itemView.findViewById(R.id.myCustomerStartTime);
            myCustomerEndTime = itemView.findViewById(R.id.myCustomerEndTime);
            workAddress = itemView.findViewById(R.id.myCustomerWorkAddress);
            myCustomerWorkTotal = itemView.findViewById(R.id.myCustomerWorkTotal);
            myCustomerInstruction = itemView.findViewById(R.id.myCustomerComment);
            myCustomerTransDate = itemView.findViewById(R.id.myCustomerTransDate);
            myCustomerOrderStatus = itemView.findViewById(R.id.myCustomerOrderStatus);
        }
    }
}
