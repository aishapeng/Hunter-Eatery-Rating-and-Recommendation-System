package com.example.hunter.ui.profile;

import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hunter.MainActivity;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel mViewModel;
    TextView username;
    ImageView profilePic;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG;
    Button options;
    MenuItem signOut;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View myLayout =  inflater.inflate(R.layout.profile_fragment, container, false);
        profilePic=myLayout.findViewById(R.id.profilePic);
        username=myLayout.findViewById(R.id.usernameProfile);
        profilePic=myLayout.findViewById(R.id.profilePic);
        options=myLayout.findViewById(R.id.options);
        signOut=myLayout.findViewById(R.id.signOut);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                username.setText(document.toObject(User.class).getUsername());
                                String photoString = document.toObject(User.class).getPhoto();
                                profilePic.setImageURI(null);
                                if(photoString!=null){
                                    Uri uri=Uri.parse(photoString);
                                    Glide.with(getContext()).load(uri).into(profilePic);
                                }
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

        options.setOnClickListener(this);

        return myLayout;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
    }

    public void showMenu(View v) {
        PopupMenu popup = new PopupMenu(getContext(), v);
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent=new Intent(getContext(), MainActivity.class);
                        startActivity(intent);
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.inflate(R.menu.profile);
        popup.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.options:
                showMenu(v);
        }
    }

}
