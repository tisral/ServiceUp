package com.example.ServiceUp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class CentralContainer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;
    private FirebaseDatabase mDatabase;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<User> userList = new ArrayList<>();
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private TextView navCurrentUserName;
    private  TextView navRating;
    private NavigationView navigationView;
    private ImageView navPicture;
    private AdView adView;


    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_central_container);

        MobileAds.initialize(this, "ca-app-pub-3940256099942544~3347511713");

        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference
        mDatabaseReference.keepSynced(true);


        toolbar = findViewById(R.id.toolbarBox);


        setSupportActionBar(toolbar);


        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        // Handling menu button activity (on the top right of the Dashboard)


        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(CentralContainer.this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);

        toggle.syncState(); // Handle screen rotation

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard(toolbar)).commit(); // Start app on message activity
            //navigationView.setCheckedItem(R.id.navMessage);
        }

        // Handling hamburger action to open the drawer onClick
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);

                    //Handling the name of the current displayed in the drawer
                    currentUserDb.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            navPicture = (ImageView) findViewById(R.id.navPicture);
                            navCurrentUserName = findViewById(R.id.nav_currentUserName);
                            navRating = findViewById(R.id.navRating);

                            String imageUrl = null;
                            imageUrl = dataSnapshot.child("image").getValue().toString();

                            if (!imageUrl.equals("")) {
                                Picasso.get().load(imageUrl).transform(new CropCircleTransformation()).into(navPicture);
                                //Picasso.get().load(imageUrl).transform(new RoundedCornersTransformation(40, 40)).into(navPicture);
                            }

                            String firstName = dataSnapshot.child("firstName").getValue().toString();
                            // String lastName = dataSnapshot.child("lastName").getValue().toString();

                            navCurrentUserName.setText(firstName.substring(0, 1).toUpperCase() + firstName.substring(1));
                            navRating.setText(dataSnapshot.child("rating").getValue().toString());

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }

            }
        });


    }

    // Handling the drawer items to redirect to the selected activity (Profile, , ...)


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        toolbar.getMenu().clear();

        switch (item.getItemId()) {

            case R.id.navProfile:
                toolbar.setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Profile()).commit();
                break;

            case R.id.navListAService:
                adView.setVisibility(View.GONE);
                toolbar.setTitle("List a Service");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ListAService()).commit();
                break;

            case R.id.navMyProviders:
                toolbar.setTitle("My Providers");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyProviders(toolbar)).commit();
                break;

            case R.id.navMyCustomers:
                toolbar.setTitle("My Customers");
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyCustomers(toolbar)).commit();
                break;

//            case R.id.navMap:
//                adView.setVisibility(View.GONE);
//                toolbar.setTitle("My Customers");
//                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Map(toolbar)).commit();
//                break;

//
        }

        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    private void setSupportActionBar(Toolbar toolbar) {

    }

    // Handling back button action
    @Override
    public void onBackPressed() {

        toolbar.setTitle("Service Up");

        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

        else {
            adView.setVisibility(View.VISIBLE);

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard(toolbar)).commit();
            //this.finish();
        }

    }

}
