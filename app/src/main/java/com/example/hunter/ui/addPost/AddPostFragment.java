package com.example.hunter.ui.addPost;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;


import com.bumptech.glide.Glide;
import com.example.hunter.Comment;
import com.example.hunter.FeedActivity;
import com.example.hunter.Like;
import com.example.hunter.Posts;
import com.example.hunter.R;

import com.example.hunter.ui.home.HomeFragment;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
//import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.sangcomz.fishbun.FishBun;
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter;
import com.sangcomz.fishbun.define.Define;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;


public class AddPostFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser fbUser;
    private ArrayList<Uri> path;
    private ImageAdapter myImageAdapter;
    private GridView gridView;
    private TextView textView;
    private Button post;
    private EditText experience;
    private RatingBar ratingBar;
    private String placeId;
    String TAG = "1";
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    PlacesClient placesClient;
    private String api="AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();



    private static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
//    private static final int RC_IMAGE_GALLERY = 2;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fbUser = FirebaseAuth.getInstance().getCurrentUser();
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "perm", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Toast.makeText(getActivity(), "here", Toast.LENGTH_LONG).show();

            FishBun.with(AddPostFragment.this).setImageAdapter(new GlideAdapter()).startAlbum();
            FishBun.with(AddPostFragment.this)
                    .setImageAdapter(new GlideAdapter())
                    .setIsUseDetailView(false)
//                    .setPickerCount(5) //Deprecated
                    .setMaxCount(5)
                    .setMinCount(1)
                    .setPickerSpanCount(6)
                    .setActionBarColor(R.color.colorPrimary)
                    .setActionBarTitleColor(R.color.colorPrimaryDark)
//                    .setArrayPaths(path)
                    .setAlbumSpanCount(2, 4)
                    .setButtonInAlbumActivity(false)
                    .setCamera(true)
                    .setReachLimitAutomaticClose(true)
                    .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_done_black_24dp))
//                    .setOkButtonDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_done_black_24dp))
                    .setAllViewTitle("All")
                    .setActionBarTitle("Image Library")
                    .textOnImagesSelectionLimitReached("Limit Reached!")
                    .textOnNothingSelected("Nothing Selected")
                    .setSelectCircleStrokeColor(Color.BLACK)
                    .isStartInAllView(false)
                    .startAlbum();
        }






    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        AddPostViewModel addPostViewModel = ViewModelProviders.of(this).get(AddPostViewModel.class);
        View root = inflater.inflate(R.layout.fragment_addpost, container, false);
        post = root.findViewById(R.id.post);
        gridView = root.findViewById(R.id.gridView);
        experience = root.findViewById(R.id.experience);
        ratingBar = root.findViewById(R.id.ratingBar);
        textView=root.findViewById(R.id.textView);
        post.setOnClickListener(this::onClick);

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (!Places.isInitialized()) {
            Places.initialize(getActivity(), api);
        }

        placesClient = Places.createClient(getContext());

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeId= place.getId();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });
        // Use fields to define the data types to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME);

        // Use the builder to create a FindCurrentPlaceRequest.
        FindCurrentPlaceRequest request =
                FindCurrentPlaceRequest.builder(placeFields).build();

        // Call findCurrentPlace and handle the response (first check that the user has granted permission).
        if (ContextCompat.checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            placesClient.findCurrentPlace(request).addOnSuccessListener(((response) -> {
                for (PlaceLikelihood placeLikelihood : response.getPlaceLikelihoods()) {
                    Log.i(TAG, String.format("Place '%s' has likelihood: %f",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                    textView.setText(String.format("Place '%s' has likelihood: %f\n",
                            placeLikelihood.getPlace().getName(),
                            placeLikelihood.getLikelihood()));
                }
            })).addOnFailureListener((exception) -> {
                if (exception instanceof ApiException) {
                    ApiException apiException = (ApiException) exception;
                    Log.e(TAG, "Place not found: " + apiException.getStatusCode());
                }
            });
        } else {
            // A local method to request required permissions;
            // See https://developer.android.com/training/permissions/requesting
            getLocationPermission();
        }



        addPostViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
//                textView.setText(s);
            }
        });
        return root;
    }
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
                    );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.post:
                Date currentTime = Calendar.getInstance().getTime();
                List<Like> likes = null;
                List<Comment> comments=null;
                String capt = experience.getText().toString();
                float rating = (ratingBar.getRating());
                Gson gson = new Gson();
                String json = gson.toJson(path);

                Posts newPost = new Posts(
                        capt,currentTime,json,mAuth.getUid(),placeId,likes,comments,rating);

                DocumentReference userDoc =db.collection("posts").document("post");
                userDoc.set(newPost).addOnCompleteListener(task -> {
                    Toast.makeText(getActivity(),"Post Successful",Toast.LENGTH_LONG).show();
                    FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container,new HomeFragment());
                    transaction.addToBackStack(null);
                    transaction.commit();
                });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        switch (requestCode) {
            case Define.ALBUM_REQUEST_CODE:
                if (resultCode == RESULT_OK) {

                    path = imageData.getParcelableArrayListExtra(Define.INTENT_PATH);
                    // you can get an image path(ArrayList<Uri>) on 0.6.2 and later
                    break;
                }
        }
        myImageAdapter = new ImageAdapter(getActivity(), path);
        gridView.setNumColumns(5);

        if(myImageAdapter.imageUrls!=null) {
            gridView.setAdapter(myImageAdapter);
        }

    }

    public class ImageAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;
        //        ImageView imageView;
        private ArrayList<Uri> imageUrls;

        public ImageAdapter(Context context, ArrayList<Uri> imageUrls) {
            super(context, R.layout.image, imageUrls);

            this.context = context;
            this.imageUrls = imageUrls;

            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;

            if (convertView == null) {  // if it's not recycled, initialize some attributes
                imageView = new ImageView(context);
                imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 0, 5, 0);
            } else {
                imageView = (ImageView) convertView;

            }

            Glide.with(getContext()).load(path.get(position)).into(imageView);

            return imageView;
        }


    }

}

