package com.example.mayank.hacknsit.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.mayank.hacknsit.R;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

/**
 * Created by Mayank on 09-04-2016.
 */
public class SignupActivity extends Activity {
    EditText mUserName;
    EditText mPassword;
    EditText mPhoneNumber;
    Button mAlreadyRegistedButton;
    Button mVerifyButton;
    String TAG = SignupActivity.class.getSimpleName();
    String sinch_app_key = "4e3e31be-3c02-48d5-8ded-628cbc4a9b6d";
    String username;
    String password;
    String phoneNumber;
    ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_signup);
        mUserName = (EditText) findViewById(R.id.name);
        mPassword = (EditText) findViewById(R.id.password);
        mAlreadyRegistedButton = (Button) findViewById(R.id.btnLinkToLoginScreen);
        mPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
        mVerifyButton = (Button) findViewById(R.id.verifyButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        mAlreadyRegistedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(i);
            }
        });
        mVerifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = mUserName.getText().toString();
                password = mPassword.getText().toString();
                phoneNumber = mPhoneNumber.getText().toString();
                username = username.trim();
                password = password.trim();
                if (username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage(R.string.signup_error_message);
                    builder.setTitle(R.string.signup_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                } else {
                    String number = "+91" + phoneNumber;
                    showProgressDialog();
                    startRegistration();
                }
            }
        });
    }

    private void showProgressDialog() {
        mProgressBar.setVisibility(ProgressBar.VISIBLE);
    }

    private void hideProgressDialog() {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    private void startRegistration() {
        setProgressBarIndeterminateVisibility(true);
        ParseUser newUser = new ParseUser();
        Log.d(TAG, "Username : " + phoneNumber);
        Log.d(TAG, "Full Name : " + username);
        Log.d(TAG, "Password : " + password);
        Log.d(TAG, "Phone Number : " + phoneNumber);
        newUser.setUsername(username);
        newUser.put("Full_Name", username);
        newUser.setPassword(password);
        newUser.put("Phone_Number", phoneNumber);
        showProgressDialog();
        newUser.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                hideProgressDialog();
                setProgressBarIndeterminateVisibility(false);
                if (e == null) {
                    Intent i = new Intent(SignupActivity.this, MainActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    e.printStackTrace();
                    builder.setMessage(e.getMessage());
                    builder.setTitle(R.string.signup_error_title);
                    builder.setPositiveButton(android.R.string.ok, null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

}
