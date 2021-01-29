package com.example.ServiceUp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import jp.wasabeef.picasso.transformations.RoundedCornersTransformation;

import static android.app.Activity.RESULT_OK;


public class Profile extends Fragment {

    private ImageButton image;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private EditText address;
    private EditText city;
    private EditText state;
    private EditText zipCode;
    private Button save;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference currentUserDb;
    private FirebaseDatabase mDatabase;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Uri resultUri = null;
    private static final int GALLERY_CODE = 1;
    private MaterialDialogs alertDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_profile, container, false);

        image = (ImageButton) view.findViewById(R.id.imageButton);
        firstName = (EditText) view.findViewById(R.id.profileFirstName);
        lastName = (EditText) view.findViewById(R.id.profileLastName);
        phoneNumber = (EditText) view.findViewById(R.id.profilePhoneNumber);
        address = (EditText) view.findViewById(R.id.profileAddress);
        city = (EditText) view.findViewById(R.id.profileCity);
        state = (EditText) view.findViewById(R.id.profileState);
        zipCode = (EditText) view.findViewById(R.id.profileZipCode);
        save = (Button) view.findViewById(R.id.profileSave);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");

        currentUserDb = mDatabaseReference.child(mUser.getUid());  // This is the current user reference
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("Profile_Pics");

        mDatabaseReference.keepSynced(true);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
                // System.out.println("I AM HERE. YOU WILL SEE--------------------------------");
            }
        });

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

                String imageUrl = null;
                imageUrl = dataSnapshot.child("image").getValue().toString();

                //System.out.println(imageUrl);
                if (!imageUrl.equals("")) {
                    //Picasso.get().load(imageUrl).transform(new CropCircleTransformation()).into(image);
                    Picasso.get().load(imageUrl).transform(new RoundedCornersTransformation(40, 40)).into(image);
                }

                firstName.setText(dataSnapshot.child("firstName").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("firstName").getValue().toString().substring(1));
                lastName.setText(dataSnapshot.child("lastName").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("lastName").getValue().toString().substring(1));
                phoneNumber.setText(dataSnapshot.child("phoneNumber").getValue().toString());
                address.setText(dataSnapshot.child("address").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("address").getValue().toString().substring(1));
                city.setText(dataSnapshot.child("city").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("city").getValue().toString().substring(1));
                state.setText(dataSnapshot.child("state").getValue().toString().substring(0, 1).toUpperCase() + dataSnapshot.child("state").getValue().toString().substring(1));
                zipCode.setText(dataSnapshot.child("zipCode").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    //Handling Dialog message before saving changes
    private void showBasicResDialog() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Save Changes")
                .setMessage("Do you want to save your changes?")

                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startSaving();
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                })
                .create().show();
    }

    private void requiredFields() {

        new MaterialAlertDialogBuilder(getContext(), R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Input Missing!!!")
                .setMessage("You must fill in all required fields")
                .create().show();

    }


    private void startSaving() {

        final String firstNameStr = firstName.getText().toString().trim();
        final String lastNameStr = lastName.getText().toString().trim();
        final String phoneNumberStr = phoneNumber.getText().toString().trim();
        final String addressStr = address.getText().toString().trim();
        final String cityStr = city.getText().toString().trim();
        final String stateStr = state.getText().toString().trim();
        final String zipCodeStr = zipCode.getText().toString().trim();


        if (!TextUtils.isEmpty(firstNameStr) && !TextUtils.isEmpty(lastNameStr) && !TextUtils.isEmpty(addressStr) && !TextUtils.isEmpty(cityStr) && !TextUtils.isEmpty(stateStr)
                && !TextUtils.isEmpty(zipCodeStr)) {

            //start the uploading...
            //mImageUri.getLastPathSegment() == /image/myphoto.jpeg"


            //Upload picture to firebase

            if (resultUri != null) {
                StorageReference imagePath = mFirebaseStorage.child("Profile_Pics").child(resultUri.getLastPathSegment());
                imagePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        final Task<Uri> firebaseUri = taskSnapshot.getStorage().getDownloadUrl();
                        firebaseUri.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                String url = firebaseUri.getResult().toString();

                                currentUserDb.child("image").setValue(url);

                            }
                        });


                    }
                });
            }

            currentUserDb.child("firstName").setValue(firstNameStr.toLowerCase());
            currentUserDb.child("lastName").setValue(lastNameStr.toLowerCase());
            currentUserDb.child("phoneNumber").setValue(phoneNumberStr.toLowerCase());
            currentUserDb.child("address").setValue(addressStr.toLowerCase());
            currentUserDb.child("city").setValue(cityStr.toLowerCase());
            currentUserDb.child("state").setValue(stateStr.toLowerCase());
            currentUserDb.child("zipCode").setValue(zipCodeStr.toLowerCase());

            Intent intent = new Intent(getActivity(), CentralContainer.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            getActivity().finish();

            //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit(); // Start app on message activity

        } else {
            requiredFields();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri mImageUri = data.getData();

            CropImage.activity(mImageUri)
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(getContext(), this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();

                //image.setImageURI(resultUri);
                Picasso.get().load(resultUri).transform(new RoundedCornersTransformation(40, 40)).into(image);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
