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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
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

public class MyProviderRecyclerAdapter extends RecyclerView.Adapter<MyProviderRecyclerAdapter.ViewHolder> {


    private Context context;
    private List<Work> workList;
    private List<Work> fullWorkList;
    private List<User> userList;
    public Toolbar toolbar;

    private Boolean startOrEnd;
    private String startTime;
    private String endTime;
    private StringBuilder timeBuilder = new StringBuilder("");
    private String phoneNumber;
    private String email;
    private String providerFirstName;


    private DatabaseReference mDatabaseReference;
    private DatabaseReference myProvider;
    private DatabaseReference mDatabaseWork;
    private DatabaseReference currentWork;


    private static final int REQUEST_CALL = 1;


    public MyProviderRecyclerAdapter(Context context, List<Work> fullWorkList, List<Work> workList, List<User> userList, Toolbar toolbar) {
        this.context = context;
        this.workList = workList;
        this.fullWorkList = fullWorkList;
        this.userList = userList;
        this.toolbar = toolbar;
        this.mDatabaseWork = FirebaseDatabase.getInstance().getReference().child("MWorks");
        this.mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("MUsers");
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_provider_row, parent, false);

        ViewHolder evh = new ViewHolder(view, context);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        final Work thisWork = workList.get(position);

        String firstName = "";
        String lastName = "";


        for (int i = 0; i < userList.size(); i++) {
            if (thisWork.getProviderId().equals(userList.get(i).getUserId())) {
                firstName = userList.get(i).getFirstName().substring(0, 1).toUpperCase() + userList.get(i).getFirstName().substring(1);
                lastName = userList.get(i).getLastName().substring(0, 1).toUpperCase() + userList.get(i).getLastName().substring(1);


                holder.myProviderName.setText(firstName + " " + lastName);

                if (!userList.get(i).getImage().equals("")) {
                    Picasso.get().load(userList.get(i).getImage()).transform(new CropCircleTransformation()).into(holder.myProviderPic);
                    //Picasso.get().load(imageUrl).transform(new RoundedCornersTransformation(40, 40)).into(navPicture);
                }

                providerFirstName = firstName;
                phoneNumber = userList.get(i).getPhoneNumber();
                email = userList.get(i).getEmail();

            }

        }


        holder.myProviderProfession.setText(thisWork.getProfession().substring(0, 1).toUpperCase() + thisWork.getProfession().substring(1));
        holder.myProviderSelectedDate.setText(thisWork.getWorkDate());

        parseTime(thisWork.getStartTime(), timeBuilder);
        holder.myProviderStartTime.setText(timeBuilder);

        parseTime(thisWork.getEndTime(), timeBuilder);
        holder.myProviderEndTime.setText(timeBuilder);

        holder.workAddress.setText(thisWork.getAddress());

        holder.myProviderWorkTotal.setText(thisWork.getWorkTotal());


        if(TextUtils.isEmpty(thisWork.getInstruction()))
        {
            holder.myProviderInstruction.setVisibility(View.GONE);

        }
        else
        {
            holder.myProviderInstruction.setText(thisWork.getInstruction().substring(0, 1).toUpperCase() + thisWork.getInstruction().substring(1));
        }
//        holder.myProviderInstruction.setText(TextUtils.isEmpty(thisWork.getInstruction()) ? "" : thisWork.getInstruction().substring(0, 1).toUpperCase() + thisWork.getInstruction().substring(1));

        holder.myProvidersTransDate.setText(thisWork.getTransactionDate());


        final PopupMenu popupMenu = new PopupMenu(context, holder.popupMenuBtn);
        popupMenu.inflate(R.menu.provider_popup_menu);


        contextMenuHandler(thisWork, popupMenu);


        holder.popupMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contextMenuHandler(thisWork, popupMenu);


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.provider_popup_call:
                                callProvider();
                                break;
                            case R.id.provider_popup_email:
                                emailProvider();
                                break;
                            case R.id.provider_popup_edit:

//                                if (thisWork.getStatus()) {
                                editInstruction(thisWork);
//                                    Toast.makeText(context, "Edit", Toast.LENGTH_LONG).show();
//                                } else {
//
//                                    errorMessage("You cannot edit a service that has already been cancelled");
//
//                                }

                                break;
                            case R.id.provider_popup_review:

                                review(thisWork);


                                break;
                            case R.id.provider_popup_cancel:

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

//        if (thisWork.getStatus() != null) {
        if (thisWork.getStatus() == 0) {
            holder.myProviderOrderStatus.setText("Cancelled");
            holder.myProviderOrderStatus.setTextColor(Color.RED);
        } else if (thisWork.getStatus() == 1) {
            holder.myProviderOrderStatus.setText("On");
            holder.myProviderOrderStatus.setTextColor(Color.GREEN);
        } else if (thisWork.getStatus() == 2) {
            holder.myProviderOrderStatus.setText("Complete");
            holder.myProviderOrderStatus.setTextColor(Color.BLACK);
        } else if (thisWork.getStatus() == 3) {
            holder.myProviderOrderStatus.setText(thisWork.getRating());
            holder.myProviderOrderStatus.setTextColor(Color.BLACK);
        }

    }

    private void contextMenuHandler(Work thisWork, PopupMenu popupMenu) {

        // IF THE WORK IS CANCELLED OR IS ALREADY RATED
        if (thisWork.getStatus() == 0 || thisWork.getStatus() == 3) {
            popupMenu.getMenu().findItem(R.id.provider_popup_edit).setVisible(false);
            popupMenu.getMenu().findItem(R.id.provider_popup_cancel).setVisible(false);
            popupMenu.getMenu().findItem(R.id.provider_popup_review).setVisible(false);
        }
        // IF THE WORK IS AVAILABLE
        else if (thisWork.getStatus() == 1) {
            popupMenu.getMenu().findItem(R.id.provider_popup_edit).setVisible(true);
            popupMenu.getMenu().findItem(R.id.provider_popup_cancel).setVisible(true);
            popupMenu.getMenu().findItem(R.id.provider_popup_review).setVisible(false);
        }
        // IF THE WORK TIME IS PASSED
        else if (thisWork.getStatus() == 2) {
            popupMenu.getMenu().findItem(R.id.provider_popup_review).setVisible(true);
            popupMenu.getMenu().findItem(R.id.provider_popup_cancel).setVisible(false);
            popupMenu.getMenu().findItem(R.id.provider_popup_edit).setVisible(false);
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


    private void editInstruction(final Work thisWork) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.my_provider_edit_instruction, null);

        final EditText myProviderInstruction = promptsView.findViewById(R.id.editInstruction);

        myProviderInstruction.setText(thisWork.getInstruction().equals("") ? "" : thisWork.getInstruction());


        final MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);

        materialAlertDialogBuilder.setView(promptsView);

        materialAlertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        })

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        String instructionStr = myProviderInstruction.getText().toString().trim();

//
//                        if(!TextUtils.isEmpty(instructionStr))
//                        {
                        currentWork = mDatabaseWork.child(thisWork.getOrderId());

                        currentWork.child("instruction").setValue(TextUtils.isEmpty(instructionStr) ? "" : instructionStr);


//                        {

                        toolbar.getMenu().clear();
                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.fragment_container, new MyProviders(toolbar)).commit();

                    }
                })
                .create().show();

    }


    private void review(final Work thisWork) {


        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.activity_review_a_service, null);


        final RatingBar rating = promptsView.findViewById(R.id.rating_rating_bar);

        final EditText comment = promptsView.findViewById(R.id.editComment);

        final MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);

        materialAlertDialogBuilder.setView(promptsView);

        materialAlertDialogBuilder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        })

                .setPositiveButton("SUBMIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        currentWork = mDatabaseWork.child(thisWork.getOrderId());

                        currentWork.child("rating").setValue(String.valueOf(rating.getRating()));

                        String commentStr = comment.getText().toString().trim();
                        currentWork.child("comment").setValue(TextUtils.isEmpty(commentStr) ? "" : commentStr);
                        currentWork.child("status").setValue(3);

                        if (!TextUtils.isEmpty(commentStr) && rating.getRating() == 0) {
                            Toast.makeText(context, "Cannot submit a comment with an empty rating", Toast.LENGTH_LONG).show();
                        } else {

                            myProvider = mDatabaseReference.child(thisWork.getProviderId());

                            double sum = 0;
                            double rate = 0;
                            int workRated = 0;

                            for (int j = 0; j < fullWorkList.size(); j++) {



                                if(fullWorkList.get(j).getOrderId().equals(thisWork.getOrderId()))
                                {
                                    fullWorkList.get(j).setRating(String.valueOf(rating.getRating()));
                                    fullWorkList.get(j).setStatus(3);
                                }

                                if (fullWorkList.get(j).getProviderId().equals(thisWork.getProviderId()) && fullWorkList.get(j).getStatus() == 3) {
                                     workRated++;

                                    sum += Float.valueOf(fullWorkList.get(j).getRating());

                                }

                                rate = sum / workRated;

                            }

                            myProvider.child("rating").setValue(String.format("%.1f", rate));

                            Toast.makeText(context, "Submitted", Toast.LENGTH_LONG).show();
                        }

                        toolbar.getMenu().clear();
                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.fragment_container, new MyProviders(toolbar)).commit();
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

//    private void errorMessage(String message) {
//
//        new MaterialAlertDialogBuilder(context, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
//                .setTitle("Error!!!")
//                .setMessage(message)
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
                        currentWork.child("cancellerId").setValue(thisWork.getCustomerId());
                        currentWork.child("cancellationDate").setValue(String.valueOf(System.currentTimeMillis()));


                        toolbar.getMenu().clear();
                        FragmentManager manager = ((AppCompatActivity) context).getSupportFragmentManager();
                        manager.beginTransaction().replace(R.id.fragment_container, new MyProviders(toolbar)).commit();

                        //notifyDataSetChanged(); // Very important to stay HERE
                        Toast.makeText(context, "Order cancelled", Toast.LENGTH_LONG).show();


                    }
                })
                .create().show();
//
//        } else {
//            errorMessage("This service is already cancelled");
//        }


    }


    private void callProvider() {
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

    private void emailProvider() {
        String recipientList = email;
        String[] recipients = recipientList.split(",");

        String subject = "Info Request";
        String message = "Hello " + providerFirstName + ",\n\n";

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


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView popupMenuBtn;
        public ImageView myProviderPic;
        public TextView myProviderName;
        public TextView myProviderProfession;
        public TextView myProviderSelectedDate;
        public TextView myProviderStartTime;
        public TextView myProviderEndTime;
        public TextView workAddress;
        public TextView myProviderWorkTotal;
        public TextView myProviderInstruction;
        public TextView myProvidersTransDate;
        public TextView myProviderOrderStatus;


        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            popupMenuBtn = itemView.findViewById(R.id.myProviderPopupMenuBtn);
            myProviderPic = itemView.findViewById(R.id.myProviderPic);
            myProviderName = itemView.findViewById(R.id.myProviderName);
            myProviderProfession = itemView.findViewById(R.id.myProviderProfession);
            myProviderSelectedDate = itemView.findViewById(R.id.myProviderSelectedDate);
            myProviderStartTime = itemView.findViewById(R.id.myProviderStartTime);
            myProviderEndTime = itemView.findViewById(R.id.myProviderEndTime);
            workAddress = itemView.findViewById(R.id.workAddress);
            myProviderWorkTotal = itemView.findViewById(R.id.myProviderWorkTotal);
            myProviderInstruction = itemView.findViewById(R.id.myProviderInstruction);
            myProvidersTransDate = itemView.findViewById(R.id.myProviderTransDate);
            myProviderOrderStatus = itemView.findViewById(R.id.myProviderOrderStatus);

        }
    }


}
