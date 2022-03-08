package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.example.myapplication.model.Users;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseAuth mAuth;
    NavigationView navigationView;
    DrawerLayout drawerLayout;
    FirebaseUser currentUser,users;
    DatabaseReference myRef2,ref;
    TabsAccessorAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.sailorBlue));

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);

        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);

        mAuth = FirebaseAuth.getInstance();
        Intent intent = new Intent(this, LogInActivity.class);
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(intent);
            finish();
        }

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        //Tablelayouts

        ViewPager2 viewPager2 = findViewById(R.id.view_pager);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        FragmentManager fm = getSupportFragmentManager();
        adapter = new TabsAccessorAdapter(fm,getLifecycle());
        viewPager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText("Chats"));
        tabLayout.addTab(tabLayout.newTab().setText("Contacts"));



        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });


        //Users and database

        users = mAuth.getCurrentUser();

        if (users != null) {
            myRef2 = FirebaseDatabase
                    .getInstance()
                    .getReference("MyUsers")
                    .child(users.getUid());

            ref = FirebaseDatabase
                    .getInstance()
                    .getReference("MyUsers")
                    .child(users.getUid()).child("Status");
            ref.setValue("Online");
        }
        myRef2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);
//                Toast.makeText(MainActivity.this, "User Login"+users.getUserName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }


    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);

        }
        else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.log_out:
                mAuth.signOut();
                Intent loIntent = new Intent(MainActivity.this,LogInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loIntent);
                break;
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this,updateActivity.class);
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        ref.setValue("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ref.setValue("Online");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ref.setValue("Offline");
    }
}

