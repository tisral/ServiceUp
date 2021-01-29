package com.example.ServiceUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText firstName;
    private EditText lastName;
    private TextInputLayout signUpUsernameInput;
    private EditText userName;
    private TextInputLayout signUpPasswordInput;
    private EditText password;
    private TextInputLayout signUpRepeatPasswordInput;
    private EditText repeatPassword;
    private EditText phoneNumber;
    private EditText address;
    private EditText city;
    private EditText state;
    private EditText zipCode;
    private EditText profession;
    private Button signUpButton;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private StorageReference mFirebaseStorage;
    private boolean invalidPassword = false;
    private boolean doNotMatchPassword = false;

    @Override
    public void onBackPressed() {
        // do what you want to do when the "back" button is pressed.
        startActivity(new Intent(CreateAccountActivity.this, MainActivity.class));
        finish();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        firstName = (EditText) findViewById(R.id.signUpFirstName);
        lastName = (EditText) findViewById(R.id.signUpLastName);
        signUpUsernameInput = findViewById(R.id.signUpUsernameInput);
        userName = (EditText) findViewById(R.id.signUpUsername);
        signUpPasswordInput = findViewById(R.id.signUpPasswordInput);
        password = (EditText) findViewById(R.id.signUpPassword);
        signUpRepeatPasswordInput = findViewById(R.id.signUpRepeatPasswordInput);
        repeatPassword = (EditText) findViewById(R.id.signUpRepeatPassword);
        phoneNumber = (EditText) findViewById(R.id.signUpPhoneNumber);
        address = (EditText) findViewById(R.id.signUpAddress);
        city = (EditText) findViewById(R.id.signUpCity);
        state = (EditText) findViewById(R.id.signUpState);
        zipCode = (EditText) findViewById(R.id.signUpZip);
        profession = (EditText) findViewById(R.id.signUpProfession);
        signUpButton = (Button) findViewById(R.id.signUpButton);


        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("MUsers");
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("Profile_Pics");
        mAuth = FirebaseAuth.getInstance();


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createNewAccount();

                mAuth.signOut();
            }
        });

//
//        userName.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//
//
//                return true;
//            }
//        });


        userName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                signUpUsernameInput.setHint("Enter an email address");

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (password.getText().toString().length() < 6) {
                    signUpPasswordInput.setError(getString(R.string.signup_error_password));
                    invalidPassword = true;
                } else {
                    signUpPasswordInput.setError(null); //Clear the error
                    invalidPassword = false;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        repeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (password.getText().toString().equals(repeatPassword.getText().toString())) {
                    signUpRepeatPasswordInput.setError(null); //Clear the error
                    doNotMatchPassword = false;
                } else {
                    signUpRepeatPasswordInput.setError(getString(R.string.signUp_passwordErrorMessage));
                    doNotMatchPassword = true;
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private void requiredFields() {

        new MaterialAlertDialogBuilder(CreateAccountActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Input Missing!!!")
                .setMessage("You must fill in all required fields")
                .create().show();

    }

    //Handling Dialog message before saving changes
    private void wrongPassword() {

        new MaterialAlertDialogBuilder(CreateAccountActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Wrong Passwords!!!")
                .setMessage("Check both passwords")
                .create().show();

    }

    private void wrongUserName() {

        new MaterialAlertDialogBuilder(CreateAccountActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog)
                .setTitle("Wrong Username!!!")
                .setMessage("Check username")
                .create().show();

    }


    private void createNewAccount() {

        final String firstNameStr = firstName.getText().toString().trim();
        final String lastNameStr = lastName.getText().toString().trim();
        final String userNameStr = userName.getText().toString().trim();
        final String passwordStr = password.getText().toString().trim();
        final String repeatPasswordStr = repeatPassword.getText().toString().trim();
        final String phoneNumberStr = phoneNumber.getText().toString().trim();
        final String addressStr = address.getText().toString().trim();
        final String cityStr = city.getText().toString().trim();
        final String stateStr = state.getText().toString().trim();
        final String zipCodeStr = zipCode.getText().toString().trim();
        final String professionStr = profession.getText().toString().trim();


        // Profession and Phone Number are not required to sign in
        if (!TextUtils.isEmpty(firstNameStr) && !TextUtils.isEmpty(lastNameStr) && !TextUtils.isEmpty(userNameStr) && !TextUtils.isEmpty(passwordStr)
                && !TextUtils.isEmpty(repeatPasswordStr) && !TextUtils.isEmpty(addressStr) && !TextUtils.isEmpty(cityStr) && !TextUtils.isEmpty(stateStr)
                && !TextUtils.isEmpty(zipCodeStr) && !TextUtils.isEmpty(professionStr)) {

            if (invalidPassword == false && doNotMatchPassword == false) {

                // TO DO : progress bar

                mAuth.createUserWithEmailAndPassword(userNameStr, passwordStr).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        if (authResult != null) {

                            String userId = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentUserDb = mDatabaseReference.child(userId);

                            currentUserDb.child("userId").setValue(userId);
                            currentUserDb.child("image").setValue("");
                            currentUserDb.child("firstName").setValue(firstNameStr.toLowerCase());
                            currentUserDb.child("lastName").setValue(lastNameStr.toLowerCase());
                            currentUserDb.child("email").setValue(userNameStr.toLowerCase());
                            currentUserDb.child("phoneNumber").setValue(phoneNumberStr.toLowerCase());
                            currentUserDb.child("address").setValue(addressStr.toLowerCase());
                            currentUserDb.child("city").setValue(cityStr.toLowerCase());
                            currentUserDb.child("state").setValue(stateStr.toLowerCase());
                            currentUserDb.child("zipCode").setValue(zipCodeStr.toLowerCase());
                            currentUserDb.child("profession").setValue(professionStr.toLowerCase());
                            currentUserDb.child("jobDescription").setValue("");
                            currentUserDb.child("rating").setValue("0.0");
                            currentUserDb.child("rate").setValue(0);

                            currentUserDb.child("startTime").setValue("");
                            currentUserDb.child("endTime").setValue("");

                            HashMap<String, Boolean> availability = new HashMap<>();

                            availability.put("monday", false);
                            availability.put("tuesday", false);
                            availability.put("wednesday", false);
                            availability.put("thursday", false);
                            availability.put("friday", false);
                            availability.put("saturday", false);
                            availability.put("sunday", false);

                            currentUserDb.child("availability").setValue(availability);


                            Intent intent = new Intent(CreateAccountActivity.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            //mAuth.signOut();
                        }

                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        wrongUserName();
                    }
                });
            } else {
                wrongPassword();
            }

        } else {
            requiredFields();
        }


    }


}
