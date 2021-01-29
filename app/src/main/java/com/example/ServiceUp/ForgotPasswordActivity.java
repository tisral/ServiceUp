package com.example.ServiceUp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText email;
    private Button submitEmailButton;
    private FirebaseAuth mAuth;


    @Override
    public void onBackPressed() {
        // do what you want to do when the "back" button is pressed.
        startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);



        email = (EditText) findViewById(R.id.tfUserEmail);
        submitEmailButton = (Button) findViewById(R.id.submitEmailButton);

        mAuth = FirebaseAuth.getInstance();

        submitEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString().trim();

                if (TextUtils.isEmpty(emailStr)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Please enter your valid email in the field above!",Toast.LENGTH_SHORT).show();
                } else {
                    mAuth.sendPasswordResetEmail(emailStr).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your email account for your email reset instructions!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this, MainActivity.class));
                            } else {
                                String errorMessage = task.getException().getMessage();
                                Toast.makeText(ForgotPasswordActivity.this, "You email submission failed! " + errorMessage,Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
