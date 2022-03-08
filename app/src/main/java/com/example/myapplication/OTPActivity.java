package com.example.myapplication;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;
import java.util.concurrent.TimeUnit;

public class OTPActivity extends AppCompatActivity {
    private EditText edit_otp;
    FirebaseAuth mAuth;
    String verificationCodeBySystem,number;
    private Button verify_otp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        mAuth = FirebaseAuth.getInstance();

        Toolbar toolbar3 = findViewById(R.id.toolbar3);
        setSupportActionBar(toolbar3);
        toolbar3.setTitle("Verify OTP");
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.sailorBlue));

        number = getIntent().getStringExtra("Number");
        System.out.println(number);
        edit_otp =  findViewById(R.id.edit_otp);
        verify_otp =  findViewById(R.id.verify_otp);
        sendOTP(number);
        String codeByUser = edit_otp.getText().toString();
        verify_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyCode(codeByUser);
            }
        });
    }

    private void sendOTP(String Number) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(mAuth)
                        .setPhoneNumber(number)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setCallbacks(mCallBacks)
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private OnVerificationStateChangedCallbacks mCallBacks = new OnVerificationStateChangedCallbacks() {
        public void onCodeSent(String s, ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifyCode(code);
                signIn(phoneAuthCredential);
            }
        }

        public void onVerificationFailed(FirebaseException e) {
//            Toast.makeText(OTPActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };
    
    private void signIn(PhoneAuthCredential credential)  {
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent regIntent = new Intent(OTPActivity.this,RegisterActivity.class);
                    regIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(regIntent);
//                    Intent intent = new Intent(OTPActivity.this,MainActivity.class);
//                    startActivity(intent);
                }
                else {
                    Toast.makeText(OTPActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void verifyCode(String codeByUser) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem,codeByUser);
        signIn(credential);
    }
}
