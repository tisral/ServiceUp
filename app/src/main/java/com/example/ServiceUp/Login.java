package com.example.ServiceUp;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends Fragment {

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText loginUsername;
    private EditText loginPassword;
    private MaterialButton loginBtn;
    private MaterialButton createAccountBtn;
    private TextInputLayout passwordTextInput;
    private MaterialButton forgotPasswordBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_login, container, false);

        loginUsername = view.findViewById(R.id.loginUserName);
        loginPassword = view.findViewById(R.id.loginPassword);
        loginBtn = view.findViewById(R.id.loginButton);
        createAccountBtn = view.findViewById(R.id.createAccount);
        passwordTextInput = view.findViewById(R.id.password_text_input);
        forgotPasswordBtn = view.findViewById(R.id.forgotPassword);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mUser = firebaseAuth.getCurrentUser();

                if (mUser != null) {
                    // This should remain here so that the next time the user open the app, no login is needed

                    //getFragmentManager().beginTransaction().replace(R.id.fragment_container, new Dashboard()).commit(); // Start app on message activity
                    startActivity(new Intent(getActivity(), CentralContainer.class));
                    getActivity().finish();
                }
            }
        };

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernameStr = loginUsername.getText().toString();
                String passwordStr = loginPassword.getText().toString();

                // Navigate to the next Fragment
                if (!(usernameStr.equals("")) && !(passwordStr.equals(""))) {
                    mAuth.signInWithEmailAndPassword(usernameStr, passwordStr).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {

                                passwordTextInput.setError(getString(R.string.login_error_password));
                                Toast.makeText(getActivity(), "Failed sign in", Toast.LENGTH_LONG).show();
                            } else {
                                passwordTextInput.setError(null); // Clear the error
                                Toast.makeText(getActivity(), "Welcome", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }

                else
                {
                    passwordTextInput.setError(getString(R.string.login_error_password));
                }

            }


        });


        createAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), CreateAccountActivity.class));
                getActivity().finish();

            }
        });

        forgotPasswordBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ForgotPasswordActivity.class));
                getActivity().finish();
            }
        });


        return view;

    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}









