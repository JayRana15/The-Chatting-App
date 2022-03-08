package com.example.myapplication;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.model.Users;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;

public class updateActivity extends AppCompatActivity {


    EditText updateUN;
    ImageView updateDP;
    Button updateBTN;
    DatabaseReference reference;
    FirebaseUser fuser;
    String temp2;


    //profile image
    FirebaseStorage storage;
    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    Uri imageUri;
    private StorageTask uploadTask;
    ProgressBar pb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        Toolbar toolbar = findViewById(R.id.toolbar6);
        setSupportActionBar(toolbar);
        setTitle("Profile");


        updateDP = findViewById(R.id.uDPIV);
        updateUN = findViewById(R.id.uUNET);
        updateBTN = findViewById(R.id.updateBTN);
        storage = FirebaseStorage.getInstance();
        pb = findViewById(R.id.pb);
        pb.setVisibility(View.GONE);



        fuser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid());


        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                updateUN.setText(user.getUserName());
                temp2 = user.getImageURl();

                if (user.getImageURl().equals("default")){
                    updateDP.setImageResource(R.drawable.ic_baseline_person_24);
                    updateDP.setBackgroundResource(R.drawable.background1);
                } else {
                    Glide.with(getBaseContext()).load(user.getImageURl()).into(updateDP);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        updateDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGetContent.launch("image/*");
            }
        });

        updateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImage();
                updateun();
            }
        });


    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>() {
                @Override
                public void onActivityResult(Uri result) {
                    if (result != null) {
                        updateDP.setImageURI(result);
                        imageUri = result;
                    }
                }
            });

    public void updateun() {

        String temp = updateUN.getText().toString();

        updateUN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String newUN = updateUN.getText().toString();
                if (!temp.equals(newUN)) {
                    temp(newUN);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void temp(String newUn){

        if (!newUn.isEmpty()) {
            DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid()).child("userName");
            newRef.setValue(newUn).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(updateActivity.this, "Username updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(updateActivity.this,e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void uploadImage() {
        if (imageUri != null && temp2 != imageUri.toString()) {
            pb.setVisibility(View.VISIBLE);
            DatabaseReference newRef1 = FirebaseDatabase.getInstance().getReference("MyUsers").child(fuser.getUid()).child("imageURl");
            storageReference = FirebaseStorage.getInstance().getReference().child("Images/"+ fuser.getUid());
            storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        pb.setVisibility(View.GONE);
                        newRef1.setValue(imageUri.toString());
                        Toast.makeText(updateActivity.this, "Image uploaded.", Toast.LENGTH_SHORT).show();
                    } else {
                        pb.setVisibility(View.GONE);
                        Toast.makeText(updateActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}