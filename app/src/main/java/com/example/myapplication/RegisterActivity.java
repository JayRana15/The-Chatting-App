package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private EditText userNameEdit;
    private Button regBtn;
    FirebaseAuth auth;
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //toolbar
        Toolbar toolbar4 = findViewById(R.id.toolbar4);
        setSupportActionBar(toolbar4);
        setTitle("Username");

        userNameEdit = findViewById(R.id.userNameEdit);
        regBtn = findViewById(R.id.regBtn);
        auth = FirebaseAuth.getInstance();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName_text = userNameEdit.getText().toString();
                if (userName_text.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Enter Username", Toast.LENGTH_SHORT).show();
                }
                else {
                    RegisterNow(userName_text);
                }
            }
        });

    }

    private void RegisterNow(final String userName) {
        FirebaseUser firebaseUser = auth.getCurrentUser();
        String userID = firebaseUser.getUid();

        myRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(userID);


        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("id",userID);
        hashMap.put("userName",userName);
        hashMap.put("imageURl","default");
        hashMap.put("status","offline");


        myRef.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Intent mi = new Intent(RegisterActivity.this,MainActivity.class);
                    mi.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mi);
                    finish();
                }
            }
        });

    }

}