package com.example.ServiceUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Dashboard extends Fragment {

    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;
    private FirebaseDatabase mDatabase;
    private ServiceRecyclerAdapter serviceRecyclerAdapter;
    private RecyclerView recyclerView;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<User> userList = new ArrayList<>();
    private Toolbar toolbar;
    private SearchView searchView;

    public Dashboard(Toolbar toolbar) {
        this.toolbar = toolbar;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_dashboard, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference
        mDatabaseReference.keepSynced(true);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        serviceRecyclerAdapter = new ServiceRecyclerAdapter(this.getActivity(), userList, toolbar); // Instantiating an new Adapter

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(serviceRecyclerAdapter);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL); // Adding divider between each view
        recyclerView.addItemDecoration(divider);

        toolbar.getMenu().clear();
        toolbar.setTitle("Service Up");
        toolbar.inflateMenu(R.menu.main_menu); // Passing the menu layout to the toolbar

        toolbar.setOnMenuItemClickListener(new android.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.menuSignOut:

                        if (mUser != null && mAuth != null) {
                            mAuth.signOut();

                            Toast.makeText(getActivity(), "Signed Out", Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(getActivity(), MainActivity.class));
                            getActivity().finish();
                            /* finish(); */

                        }
                        return true;

                    case R.id.menuSearch:

                        searchView = (SearchView) menuItem.getActionView();

                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {

                                List<User> filteredUserList = new ArrayList<>();

                                for (User singleUser : userList) {

                                    if (singleUser.getProfession().contains(newText.toLowerCase()) ||
                                            singleUser.getFirstName().contains(newText.toLowerCase())
                                            || singleUser.getLastName().contains(newText.toLowerCase())) {

                                        filteredUserList.add(singleUser);
                                    }

                                }

                                serviceRecyclerAdapter.filterList(filteredUserList);

                                return false;
                            }
                        });

                        return true;

                    default:
                        return true;
                }

            }
        });


        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);


                // Removing the current user from the dashboard display
                if (!user.getUserId().equals(mUser.getUid())) {
                    userList.add(user);

                    Collections.reverse(userList);

                    serviceRecyclerAdapter = new ServiceRecyclerAdapter(getActivity(), userList, toolbar);
                    recyclerView.setAdapter(serviceRecyclerAdapter);

                    serviceRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);

                // Removing the current user from the dashboard display
                if (!user.getUserId().equals(mUser.getUid())) {



                    for(int i = 0; i < userList.size(); i++)
                    {
                        if(userList.get(i).getUserId() == user.getUserId())
                        {
                            userList.remove(i);
                        }
                    }

                    userList.add(user);

                    Collections.reverse(userList);

                    serviceRecyclerAdapter = new ServiceRecyclerAdapter(getActivity(), userList, toolbar);
                    recyclerView.setAdapter(serviceRecyclerAdapter);

                    serviceRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE


                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                // serviceRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return view;
    }


//    // Handling the list of data to display in the dashboard
//    @Override
//    public void onStart() {
//        super.onStart();
//
//
//
//    }

}
