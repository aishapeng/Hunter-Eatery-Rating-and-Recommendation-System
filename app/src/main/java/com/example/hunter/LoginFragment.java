package com.example.hunter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import android.util.Log;

import static android.app.Activity.RESULT_OK;


public class LoginFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "..." ;
    private SignInButton googleLogin;
    private static final int RC_SIGN_IN = 123;
    String token;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myLayout = inflater.inflate(R.layout.fragment_login, container, false);

        googleLogin=myLayout.findViewById(R.id.googleLogin);
        googleLogin.setSize(SignInButton.SIZE_STANDARD);

        googleLogin.setOnClickListener(this);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
//        database = FirebaseDatabase.getInstance().getReference();

        final FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if(fbUser != null) {
            // User already signed in

            // get the FCM token
//            String token = FirebaseInstanceId.getInstance().getToken();
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if(!task.isSuccessful()){
                                Log.w(TAG, "getInstanceId failed", task.getException());
                                return;
                            }
                            token = task.getResult().getToken();
                        }
                    });


            // save the user info in the database to users/UID/
            // we'll use the UID as part of the path
            User user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);
            DocumentReference documentReference =db.collection("users").document();
            documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_LONG).show();
                    Intent intent= new Intent(getActivity(),FeedActivity.class);
                    intent.putExtra("userId",fbUser.getUid());
                    startActivity(intent);

                    Toast.makeText(getContext(),"FB login",Toast.LENGTH_LONG).show();
                }
            });
//            database.child("users").child(user.uid).setValue(user);

            // go to feed activity
            Intent intent= new Intent(getActivity(),FeedActivity.class);
            intent.putExtra("userId",fbUser.getUid());
            startActivity(intent);

        }
        else {
            signIn();
        }

        return myLayout;
    }
    //GOOGLE SIGN IN
    private void signIn() {

                // Get an instance of AuthUI based on the default app
                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
//                                .setIsSmartLockEnabled(false)
                                .build(),
                        RC_SIGN_IN);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in

                // get the Firebase user
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

                // get the FCM token
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if(!task.isSuccessful()){
                                    Log.w(TAG, "getInstanceId failed", task.getException());
                                    return;
                                }
                                token = task.getResult().getToken();
                            }
                        });

                // save the user info in the database to users/UID/
                // we'll use the UID as part of the path
                User user = new User(fbUser.getUid(), fbUser.getDisplayName(), token);
                DocumentReference documentReference =db.collection("users").document();
                documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(),"Login Successful",Toast.LENGTH_LONG).show();

                        Toast.makeText(getContext(),"FB login",Toast.LENGTH_LONG).show();
                    }
                });
                // go to feed activity
                Intent intent = new Intent(getContext(), FeedActivity.class);
                startActivity(intent);
            } else {
                // Sign in failed, check response for error code
                if (response != null) {
                    Toast.makeText(getContext(), response.getError().getMessage(), Toast.LENGTH_SHORT).show();
                    Intent intent= new Intent(getActivity(),FeedActivity.class);
//                    intent.putExtra("userId",fbUser.getUid());
                    startActivity(intent);
                }
            }
        }
    }


    @Override
    public void onStart(){
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        updateUI(currentUser);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.googleLogin:
                signIn();
                break;
//            case R.id.facebookLogin:
//                LoginManager.getInstance().logInWithReadPermissions(getActivity(), Arrays.asList("public_profile"));
//                break;
        }
    }


}
