package com.example.hunter;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.hunter.ui.addPost.AddPostFragment;
import com.example.hunter.ui.home.HomeFragment;
import com.example.hunter.ui.notifications.NotificationsFragment;
import com.example.hunter.ui.profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FeedActivity extends AppCompatActivity{

//    DatabaseReference database;
//    RecyclerView recyclerView;
//    RecyclerView.LayoutManager mLayoutManager;
    ImageAdapter mAdapter;
    ArrayList<Image> images = new ArrayList<Image>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    BottomNavigationView navView;
//    NavController navController;
//    AppBarConfiguration appBarConfiguration;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);
        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = new AppBarConfiguration.Builder(
//                R.id.navigation_home, R.id.navigation_addPost, R.id.navigation_notifications)
//                .build();
//        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
//        NavigationUI.setupWithNavController(navView, navController);

//        if (fbUser == null) {
//            finish();
//        }

        // Setup the RecyclerView
//        mLayoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new ImageAdapter(images, this);
//        recyclerView.setAdapter(mAdapter);
        navView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener=
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.post:
                            Toast.makeText(getApplicationContext(),"btm success",Toast.LENGTH_LONG).show();
                            selectedFragment=new AddPostFragment();
                            break;
                        case R.id.newsFeed:
                            selectedFragment=new HomeFragment();
                            break;
                        case R.id.search:
                            selectedFragment=new NotificationsFragment();
                            break;
                        case R.id.profile:
                            selectedFragment=new ProfileFragment();
                            break;
                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container,selectedFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();

                    return true;
                }
            };


}
