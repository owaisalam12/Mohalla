package com.owais.mohalla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.goodiebag.pinview.Pinview;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import libs.mjn.prettydialog.PrettyDialog;
import libs.mjn.prettydialog.PrettyDialogCallback;

public class VerificationActivity extends AppCompatActivity {
    private Pinview pinView;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;
    private String mVerificationId;
    private Button btnVerifyCode;
    TextView textViewResendCode;
    AlertDialog dialog;
    private String phone, name, address;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        mAuth = FirebaseAuth.getInstance();
        pinView = findViewById(R.id.verification_pinview);
        btnVerifyCode = findViewById(R.id.verification__button);
        textViewResendCode = findViewById(R.id.verification_resendText);
        dialog = new SpotsDialog.Builder().setContext(this)
                .setCancelable(false)
                .setTheme(R.style.Custom)
                .build();

        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        phone = intent.getStringExtra("phone");
        address = intent.getStringExtra("address");
        Log.v("verify",name);
        Log.v("verify","+92"+phone);
        Log.v("verify",address);
        sendVerificationCode(phone);

        textViewResendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationCode(phone, mResendToken);
                Toast.makeText(VerificationActivity.this, "Code has been resent.", Toast.LENGTH_SHORT).show();
            }
        });
        btnVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String code = pinView.getValue().trim();
                if (code.isEmpty() || code.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Please Enter Code", Toast.LENGTH_SHORT).show();
                    pinView.requestFocus();
                    return;
                }
               // dialog.show();
                verifyVerificationCode(code);
            }
        });
    }

    //the method is sending verification code
    //the country id is concatenated
    //you can take the country id as user input as well
    private void sendVerificationCode(String mobile) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92"+mobile,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                mCallbacks);
    }
    //the callback to detect the verification status
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

            //Getting the code sent by SMS
            final String code = phoneAuthCredential.getSmsCode();

            //sometime the code is not detected automatically
            //in this case the code will be null
            //so user has to manually enter the code

            if (code != null) {
                pinView.setValue(code);
                //verifying the code
//                verifyVerificationCode(code);


            }

        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerificationActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        @Override
        public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);

            //storing the verification id that is sent to the user
            mVerificationId = s;
            mResendToken = forceResendingToken;
        }
    };
    private void verifyVerificationCode(String code) {
        //creating the credential
        try {
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);

            //signing the user
           signInWithPhoneAuthCredential(credential);
        } catch (Exception e) {
            Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(VerificationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //verification successful we will start the profile activity
                            dialog.dismiss();
                           // addUserData();
                            //addUserDatamySql();


                            Toast.makeText(VerificationActivity.this, "Successfull!", Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(VerificationActivity.this, DashboardActivity.class);


                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else {
                            dialog.dismiss();
                            //Toast.makeText(PhoneVerifyActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            dialogError("Something went wrong", task.getException().getMessage());

                        }
                    }
                });
    }
    private void dialogError(String title, String msg) {

        final PrettyDialog pDialog = new PrettyDialog(this);
        pDialog.setCancelable(false);
        pDialog

                .setIcon(R.drawable.pdlg_icon_close)
                .setIconTint(R.color.pdlg_color_red)
                .setAnimationEnabled(false)
                .setTitle(title)
                .setMessage(msg)

                .addButton(
                        "OK",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_red,
                        new PrettyDialogCallback() {
                            @Override
                            public void onClick() {
                                pDialog.dismiss();
                            }
                        }
                )
                .show();
    }

    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+92"+phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }

}
