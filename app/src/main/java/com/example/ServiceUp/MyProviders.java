package com.example.ServiceUp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.SearchView;
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
import java.util.List;

public class MyProviders extends Fragment {


    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;

    private DatabaseReference mDatabaseWork;
    private FirebaseDatabase mDatabase;
    private MyProviderRecyclerAdapter myProviderRecyclerAdapter;
    private RecyclerView recyclerView;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private List<Work> workList = new ArrayList<>();
    private List<User> userList = new ArrayList<>();
    private List<Work> fullWorkList = new ArrayList<>();
    private Toolbar toolbar;
    private SearchView searchView;

    public MyProviders(Toolbar toolbar) {
        this.toolbar = toolbar;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_my_providers, container, false);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference

        mDatabaseWork = mDatabase.getReference().child("MWorks");

        mDatabaseWork.keepSynced(true);

        recyclerView = view.findViewById(R.id.recyclerViewMyProviders);
        recyclerView.setHasFixedSize(true);

        myProviderRecyclerAdapter = new MyProviderRecyclerAdapter(this.getActivity(), fullWorkList, workList, userList, toolbar); // Instantiating an new Adapter

        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(myProviderRecyclerAdapter);

        RecyclerView.ItemDecoration divider = new DividerItemDecoration(this.getActivity(), DividerItemDecoration.VERTICAL); // Adding divider between each view
        recyclerView.addItemDecoration(divider);


        toolbar.inflateMenu(R.menu.providers_and_customers_menu); // Passing the menu layout to the toolbar

        toolbar.setOnMenuItemClickListener(new android.widget.Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {

                switch (menuItem.getItemId()) {

                    case R.id.myProvidersSearch:

                        searchView = (SearchView) menuItem.getActionView();

                        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

                        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                            @Override
                            public boolean onQueryTextSubmit(String s) {
                                return false;
                            }

                            @Override
                            public boolean onQueryTextChange(String newText) {

                                List<Work> filteredWorkList = new ArrayList<>();

                                for (Work singleWork : workList) {

                                    if (singleWork.getProfession().contains(newText.toLowerCase()) ||
                                            singleWork.getProviderFirstName().contains(newText.toLowerCase()) ||
                                            singleWork.getProviderLastName().contains(newText.toLowerCase()) ||
                                            singleWork.getWorkDate().toLowerCase().contains(newText.toLowerCase())
                                            || String.valueOf(singleWork.getTransactionDate()).toLowerCase().contains(newText.toLowerCase())) {

                                        filteredWorkList.add(singleWork);
                                    }

                                }

                                myProviderRecyclerAdapter.filterList(filteredWorkList);

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
                if (user != null) {


                    for(int i = 0; i < userList.size(); i++)
                    {
                        if(userList.get(i).getUserId() == user.getUserId())
                        {
                            userList.remove(i);
                        }
                    }

                    userList.add(user);

                    //Collections.reverse(userList);

                    myProviderRecyclerAdapter = new MyProviderRecyclerAdapter(getActivity(), fullWorkList, workList, userList, toolbar);
                    recyclerView.setAdapter(myProviderRecyclerAdapter);

                    myProviderRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE


                }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                User user = dataSnapshot.getValue(User.class);
                if (user != null) {


                    for(int i = 0; i < userList.size(); i++)
                    {
                        if(userList.get(i).getUserId() == user.getUserId())
                        {
                            userList.remove(i);
                        }
                    }

                    userList.add(user);

                    myProviderRecyclerAdapter = new MyProviderRecyclerAdapter(getActivity(), fullWorkList, workList, userList, toolbar);
                    recyclerView.setAdapter(myProviderRecyclerAdapter);

                    myProviderRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE


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



        mDatabaseWork.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Work work = dataSnapshot.getValue(Work.class);

                fullWorkList.add(work);

                System.out.println(work.getOrderId());

                 if (work.getCustomerId().equals(mUser.getUid())) {

                        workList.add(work);

                        myProviderRecyclerAdapter = new MyProviderRecyclerAdapter(getActivity(), fullWorkList, workList, userList, toolbar);
                        recyclerView.setAdapter(myProviderRecyclerAdapter);

                        myProviderRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE

                    }


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                Work work = dataSnapshot.getValue(Work.class);

                fullWorkList.add(work);
                System.out.println(work.getOrderId());

                if (work.getCustomerId().equals(mUser.getUid())) {

                    for(int i = 0; i < workList.size(); i++)
                    {
                        //REMOVE THE WORK THAT JUST GOT CHANGED
                        if(workList.get(i).getOrderId() == work.getOrderId())
                        {
                            workList.remove(i);
                        }
                    }

                    workList.add(work);

                    myProviderRecyclerAdapter = new MyProviderRecyclerAdapter(getActivity(), fullWorkList, workList, userList, toolbar);
                    recyclerView.setAdapter(myProviderRecyclerAdapter);

                    myProviderRecyclerAdapter.notifyDataSetChanged(); // Very important to stay HERE
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
}