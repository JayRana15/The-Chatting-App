package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.Adapter.MessageAdapter;
import com.example.myapplication.model.Chat;
import com.example.myapplication.model.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    TextView username,status;
    ImageView imageView;

    RecyclerView recyclerView;
    EditText msg_editText;
    ImageButton sendBtn;


    FirebaseUser fUser;
    DatabaseReference reference;
    Intent intent;
    String userid;


    MessageAdapter messageAdapter;
    List<Chat> mchat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);


        //action bar
        Toolbar toolbar5 = findViewById(R.id.toolbar5);
        setSupportActionBar(toolbar5);
        setTitle("");

        imageView = findViewById(R.id.profileImage);
        username = findViewById(R.id.username_profile);
        status = findViewById(R.id.status);


        sendBtn = findViewById(R.id.btn_send);
        msg_editText = findViewById(R.id.text_send);
        recyclerView = findViewById(R.id.recycler_View);


        //messages
        intent = getIntent();
        userid = intent.getStringExtra("userid");

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("MyUsers").child(userid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                username.setText(user.getUserName());
                status.setText(user.getStatus());

                if(user.getImageURl().equals("default")) {
                    imageView.setImageResource(R.drawable.ic_baseline_person_24);
                }
                else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURl())
                            .into(imageView);
                }

                readMessage(fUser.getUid(),userid,user.getImageURl());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //sending message

        msg_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
               String msg = msg_editText.getText().toString().trim();
                if (msg.isEmpty()) {
                    sendBtn.setEnabled(false);
                }
                else {
                    sendBtn.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = msg_editText.getText().toString();
                sendMessage(fUser.getUid(),userid,msg);
                msg_editText.setText("");
            }
        });


        //Recycler view for messages
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);



        //adding users to chat fragment
        final DatabaseReference chatRef = FirebaseDatabase.getInstance().
                getReference("ChatList")
                .child(fUser.getUid())
                .child(userid);
        chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) {
                    chatRef.child("id").setValue(userid);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String sender, String receiver, String message) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        reference.child("Chats").push().setValue(hashMap);

    }


    private void readMessage (String myID, String userId, String imageurl) {
        mchat = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mchat.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chat chat = snapshot.getValue(Chat.class);

                    if (chat.getReceiver().equals(myID) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(myID)) {
                        mchat.add(chat);
                    }
                    messageAdapter =  new MessageAdapter(MessageActivity.this,mchat,imageurl);
                    recyclerView.setAdapter(messageAdapter);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}