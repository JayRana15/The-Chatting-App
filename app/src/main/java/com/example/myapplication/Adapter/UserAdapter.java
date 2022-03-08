package com.example.myapplication.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.MessageActivity;
import com.example.myapplication.R;
import com.example.myapplication.model.Users;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<Users> mUsers;


    public UserAdapter(Context context, List<Users> mUsers) {
        this.context = context;
        this.mUsers = mUsers;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Users users = mUsers.get(position);
        holder.username.setText(users.getUserName());

        if (users.getImageURl().equals("default")) {
            holder.imageView.setImageResource(R.drawable.ic_baseline_person_24_2);
        }
        else {
            //Adding glide library
            Glide.with(context).load(users.getImageURl()).into(holder.imageView);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, MessageActivity.class);
                i.putExtra("userid", users.getId());
                context.startActivity(i);
            }
        });


        //status check

    }


    @Override
    public int getItemCount() {
        return mUsers.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView imageView;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.name_user);
            imageView = itemView.findViewById(R.id.dp_user);


        }
    }
}
