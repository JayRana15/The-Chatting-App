package com.example.myapplication;

import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks;

public class LogInActivity extends AppCompatActivity {
    private Button get_otp_btn;
    private OnVerificationStateChangedCallbacks mCallBacks;
    private EditText edit_num;
    String phoneNumber;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        Toolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        setTitle("Log In");

        if (VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.sailorBlue));
        }
        get_otp_btn = (Button) findViewById(R.id.get_otp_btn);
        edit_num = (EditText) findViewById(R.id.edit_num);
        get_otp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LogInActivity.this, OTPActivity.class);
                String num = edit_num.getText().toString();
                phoneNumber = "+91"  + num;
                if (edit_num.length() == 10) {
                    intent.putExtra("Number", phoneNumber);
                    startActivity(intent);
                } else {
                    edit_num.setError("Enter 10 Digit Number.");
                }
            }
        });
    }
}







