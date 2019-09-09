package com.project.mathsbookapp.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.project.mathsbookapp.R;


public class LoginActivity extends AppCompatActivity {

    EditText mEmailEditText, mPasswordEditText;
    Button mLoginButton, mRegisterButton;
    String email, password, newPass, oldpass;
    ProgressDialog progressDialog;
    TextView mForgetPassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.activity_login_email_editText);
        mPasswordEditText = (EditText) findViewById(R.id.activity_login_password_editText);
        mLoginButton = (Button) findViewById(R.id.activity_login_login_button);
        mRegisterButton = (Button) findViewById(R.id.activity_login_register_button);
        mForgetPassword = (TextView) findViewById(R.id.activity_login_forgot_password_textview);


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                email = mEmailEditText.getText().toString();
                password = mPasswordEditText.getText().toString();

                if (mEmailEditText.getText().toString().length() < 1) {
                    mEmailEditText.setError("Please enter Email");

                } else if (mPasswordEditText.getText().toString().length() < 1) {
                    mPasswordEditText.setError("Please enter Password");

                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(LoginActivity.this, " Please enter Valid Email", Toast.LENGTH_SHORT).show();
                } else {
                    signIn();
                }
            }
        });


        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });




        mForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                FirebaseAuth auth = FirebaseAuth.getInstance();
                String emailAddress = mEmailEditText.getText().toString();
                auth.sendPasswordResetEmail(emailAddress)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(LoginActivity.this, "Reset link sent to your email address!!!", Toast.LENGTH_SHORT).show();
                                } else if (!task.isSuccessful()) {

                                    Toast.makeText(LoginActivity.this, "Email address is not registered!!!", Toast.LENGTH_SHORT).show();
                                } else {

                                    Toast.makeText(LoginActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


    }

    public void signIn() {

        String Email = mEmailEditText.getText().toString().trim();
        String Password = mPasswordEditText.getText().toString().trim();
        if (mEmailEditText.getText().toString().length() < 1 || mPasswordEditText.getText().toString().length() < 1) {
            Toast.makeText(LoginActivity.this, "Please provide your email and password.", Toast.LENGTH_SHORT).show();
        } else if (!(Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText().toString()).matches())) {
            Toast.makeText(LoginActivity.this, "Please provide valid email address.", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog = new ProgressDialog(LoginActivity.this);
            progressDialog.setTitle("Signing in...");
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(Email, Password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) { //login failed
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Invalid username or password.", Toast.LENGTH_SHORT).show();
                    } else if (task.isSuccessful()) { //login successful
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Successfully logged in.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, "Something went wrong. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}

