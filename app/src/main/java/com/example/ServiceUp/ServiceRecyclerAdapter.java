package com.example.ServiceUp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class ServiceRecyclerAdapter extends RecyclerView.Adapter<ServiceRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<User> userList;
    private List<User> userFullList;
    public Toolbar toolbar;
    private static final int REQUEST_CALL = 1;


    public ServiceRecyclerAdapter(Context context, List<User> userList, Toolbar toolbar) {
        this.context = context;
        this.userList = userList;
        this.userFullList = userList;
        this.toolbar = toolbar;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_provider_row, parent, false);

        ViewHolder evh = new ViewHolder(view, context);

        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        User thisUser = userList.get(position);

        holder.providerSelected = thisUser;


        if (!thisUser.getImage().equals("")) {
            Picasso.get().load(thisUser.getImage()).transform(new CropCircleTransformation()).into(holder.providerPic);
            //Picasso.get().load(imageUrl).transform(new RoundedCornersTransformation(40, 40)).into(navPicture);
        }
//        else
//        {
//            holder.providerPic.setVisibility(View.INVISIBLE);
//        }

        holder.serviceProviderFirstName = thisUser.getFirstName().substring(0, 1).toUpperCase() + thisUser.getFirstName().substring(1);
        holder.serviceProviderLastName = thisUser.getLastName().substring(0, 1).toUpperCase() + thisUser.getLastName().substring(1);


        holder.serviceProviderName.setText(holder.serviceProviderFirstName + " " + holder.serviceProviderLastName);
        holder.jobTitle.setText( thisUser.getProfession().isEmpty()? "" : thisUser.getProfession().substring(0, 1).toUpperCase() + thisUser.getProfession().substring(1));

        if(thisUser.getRate() == 0)
        {
            holder.serviceCurrency.setVisibility(View.INVISIBLE);
            holder.serviceRate.setVisibility(View.INVISIBLE);
            holder.serviceTime.setVisibility(View.INVISIBLE);
        }

        else
        {
            holder.serviceRate.setText(thisUser.getRate().toString());
        }



        if (!thisUser.getJobDescription().equals("")) {
            holder.jobDescription.setText(thisUser.getJobDescription().substring(0, 1).toUpperCase() + thisUser.getJobDescription().substring(1));
        }



        holder.rating.setText(thisUser.getRating());
        holder.phoneNumber = thisUser.getPhoneNumber();
        holder.email = thisUser.getEmail();
        holder.mtoolbar = toolbar;


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Here You Do Your Click Magic

                //Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();

                FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                manager.beginTransaction().replace(R.id.fragment_container, new addToCart(holder.providerSelected,  toolbar)).commit(); // Start app on message activity
            }
        });



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
        return userList.size();
    }

    public void filterList(List<User> filteredList) {
        userList = filteredList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView providerPic;
        public String serviceProviderFirstName;
        public String serviceProviderLastName;
        public TextView serviceProviderName;
        public TextView jobTitle;

        public TextView serviceRate;
        public TextView serviceCurrency;
        public TextView serviceTime;
        public TextView jobDescription;
        public TextView rating;
        public String phoneNumber;
        public String email;


        public LinearLayout expandableView;
        public CardView cardView;
        public ImageButton arrowBtn;
        public ImageButton dashboardCallProvider;
        public ImageButton dashboardEmailProvider;
        public ImageButton dashboardAddToCart;
        public View mView;
        private User providerSelected;
        public Toolbar mtoolbar;


        public ViewHolder(@NonNull View itemView, final Context context) {
            super(itemView);

            // context = context;

            providerPic = itemView.findViewById(R.id.providerPic);
            serviceProviderName = itemView.findViewById(R.id.serviceProviderName);
            jobTitle = itemView.findViewById(R.id.jobTitle);
            serviceCurrency = itemView.findViewById(R.id.serviceCurrency);
            serviceRate = itemView.findViewById(R.id.serviceRate);
            serviceTime = itemView.findViewById(R.id.serviceTime);
            jobDescription = itemView.findViewById(R.id.jobDescription);
            rating = itemView.findViewById(R.id.rating);
            dashboardCallProvider = itemView.findViewById(R.id.dashboardCallProvider);
            dashboardEmailProvider = itemView.findViewById(R.id.dashboardEmailProvider);
            dashboardAddToCart = itemView.findViewById(R.id.dashboardAddToCart);

            expandableView = itemView.findViewById(R.id.expandableView);
            arrowBtn = itemView.findViewById(R.id.arrowBtn);
            cardView = itemView.findViewById(R.id.cardView);
            mView = itemView;
            final Context finalContext = context;


            dashboardCallProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
            });

            dashboardEmailProvider.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String recipientList = email;
                    String[] recipients = recipientList.split(",");

                    String subject = "Info Request";
                    String message = "Hello " + serviceProviderFirstName + ",\n\n";

                    Intent intent = new Intent(Intent.ACTION_SEND); // ACTION_SENDTO

                    intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    intent.putExtra(Intent.EXTRA_TEXT, message);


                    intent.setType("application/octet-stream");
                    intent.setType("message/rfc822");
                    context.startActivity(Intent.createChooser(intent, "Choose an email client"));
                }
            });
            dashboardAddToCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FragmentManager manager = ((AppCompatActivity)context).getSupportFragmentManager();

                    manager.beginTransaction().replace(R.id.fragment_container, new addToCart(providerSelected,  mtoolbar)).commit(); // Start app on message activity

                }
            });


            arrowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (expandableView.getVisibility() == View.GONE) {
                        jobDescription.setMaxLines(13);
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        expandableView.setVisibility(View.VISIBLE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_up);
                    } else {
                        jobDescription.setMaxLines(1);
                        TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
                        expandableView.setVisibility(View.GONE);
                        arrowBtn.setBackgroundResource(R.drawable.ic_keyboard_arrow_down);
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Go to the request service activity
                }
            });


        }


    }


}
