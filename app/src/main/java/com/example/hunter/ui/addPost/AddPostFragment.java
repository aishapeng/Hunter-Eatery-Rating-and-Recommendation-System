package com.example.hunter.ui.addPost;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.EditText;

import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import com.example.hunter.Category;
import com.example.hunter.Comment;
import com.example.hunter.Eateries;
import com.example.hunter.ImageAdapter;
import com.example.hunter.Like;
import com.example.hunter.LocationTrack;
import com.example.hunter.MultiSelectionSpinner;
import com.example.hunter.Posts;
import com.example.hunter.R;

import com.example.hunter.ui.home.HomeFragment;
import com.google.android.gms.common.api.Status;

import com.google.android.gms.maps.model.LatLng;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.OpeningHours;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;



import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.app.Activity.RESULT_OK;

public class AddPostFragment extends Fragment implements View.OnClickListener {
    private static final int REQUEST_CODE_CHOOSE = 1;

    private EditText experience;
    private RatingBar ratingBar, priceBar;
    private String placeId, placeName, address, phoneNum;
    private OpeningHours openingHours;
    private int priceLvl;
    private List<PhotoMetadata> photoMetadata;
    private boolean mLocationPermissionGranted;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private List<Uri> mSelected;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private Context context;
    private ViewPager viewPager;
    private double longitude;
    private double latitude;
    private ArrayList<Category> category = new ArrayList<>();
    private MultiSelectionSpinner multiSelectionSpinner;


    private static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getActivity(), "perm", Toast.LENGTH_LONG).show();

            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
        } else {
            Matisse.from(AddPostFragment.this)
                    .choose(MimeType.ofAll())
                    .countable(true)
                    .maxSelectable(9)
                    .theme(R.style.Matisse_Dracula)
//                    .addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                    .gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                    .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                    .thumbnailScale(0.85f)
                    .imageEngine(new GlideEngine())
                    .showPreview(true) // Default is `true`
                    .forResult(REQUEST_CODE_CHOOSE);
        }

        String[] catName = getResources().getStringArray(R.array.category);

        for (int i = 0; i < catName.length; i++) {
            category.add(new Category(catName[i], false));
        }

        LocationTrack locationTrack = new LocationTrack(context);

        if (ContextCompat.checkSelfPermission(getActivity(), ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (locationTrack.canGetLocation()) {
                longitude = locationTrack.getLongitude();
                latitude = locationTrack.getLatitude();

                Toast.makeText(context, "Longitude:" + (longitude) + "\nLatitude:" + (latitude), Toast.LENGTH_SHORT).show();
            } else {
                locationTrack.showSettingsAlert();
            }

        } else {
            getLocationPermission();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_addpost, container, false);
        TextView post = root.findViewById(R.id.post);
        priceBar = root.findViewById(R.id.priceBar);
        experience = root.findViewById(R.id.experience);
        ratingBar = root.findViewById(R.id.ratingBar);
        post.setOnClickListener(this);
        context = root.getContext();
        viewPager = root.findViewById(R.id.viewPager);
        multiSelectionSpinner = root.findViewById(R.id.spinner);

        multiSelectionSpinner.setItems(category);


        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        if (!Places.isInitialized()) {
            String api = "AIzaSyDz5BGny6Lsp7gW-uJznoLVZS4riEdfnF0";
            Places.initialize(root.getContext(), api);
        }

//        PlacesClient placesClient = Places.createClient(root.getContext());

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.PHONE_NUMBER, Place.Field.OPENING_HOURS, Place.Field.PHOTO_METADATAS, Place.Field.PRICE_LEVEL));

        autocompleteFragment.setLocationBias(RectangularBounds.newInstance(
                new LatLng(latitude, longitude),
                new LatLng(latitude, longitude)));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeId = place.getId();
                placeName = place.getName();
                address = place.getAddress();
                phoneNum = place.getPhoneNumber();
                openingHours = place.getOpeningHours();
                photoMetadata = place.getPhotoMetadatas();
            }

            @Override
            public void onError(@NonNull Status status) {
            }
        });

        return root;
    }

    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
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
        switch (v.getId()) {
            case R.id.post:
                Date currentTime = Calendar.getInstance().getTime();
                List<Like> likes = null;
                List<Comment> comments = null;
                String capt = experience.getText().toString();
                Double rating = Double.parseDouble(String.valueOf(ratingBar.getRating()));
                Double price = Double.parseDouble(String.valueOf(priceBar.getRating()));
                int point = (mSelected.size() * 3) + (10);
                ArrayList<Category> selectedItem = multiSelectionSpinner.getSelectedItems();
                List<String> cat = new ArrayList<>();
                for (int i = 0; i < selectedItem.size(); i++) {
                    cat.add(selectedItem.get(i).getName());
                }
                String postid = mAuth.getUid() + placeId + currentTime.toString();
                HashMap<String, String> postID = new HashMap<>();
                postID.put("postID", postid);
                Posts newPost = new Posts(
                        postid, capt, currentTime, mAuth.getUid(), placeId, likes, comments, rating, price);
                List<Posts> postsArrayList = new ArrayList<>();
                postsArrayList.add(newPost);
                Eateries newEatery = new Eateries(address, cat, openingHours, phoneNum, photoMetadata, placeId, placeName, price, rating, price, rating);
                DocumentReference placeRef = db.collection("eateries").document(placeId);

                placeRef.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {

                            placeRef.collection("posts").document(postid).set(postID).addOnCompleteListener(task1 -> {
                                placeRef.update("postNum", FieldValue.increment(1), "totalRate", FieldValue.increment(rating), "totalPrice", FieldValue.increment(price)).addOnCompleteListener(task2 -> {
                                    List<String> group = (List<String>) document.get("category");
                                    Double totalRate = document.getDouble("totalRate");
                                    Double totalPri = document.getDouble("totalPrice");
                                    db.collection("posts").document(postid).set(newPost).addOnCompleteListener(task3 -> {
                                        Double rate = (totalRate + rating) / (Double.parseDouble(document.get("postNum").toString())+1);
                                        Long priLvl = Math.round((totalPri + price) / (Double.parseDouble(document.get("postNum").toString())+1));
                                        placeRef.update("rating", rate, "price_level", priLvl);
                                        for (int i = 0; i < cat.size(); i++) {
                                            for (int j=0;j<group.size();j++){
                                                if(!cat.get(i).equals(group.get(j))){
                                                    placeRef.update("category",FieldValue.arrayUnion(cat.get(i)));
                                                }
                                            }
                                        }
                                        for (int i = 0; i < mSelected.size(); i++) {
                                            db.collection("posts").document(postid).update("photo_id", FieldValue.arrayUnion(mSelected.get(i).toString()));
                                        }
                                    });
                                });
                            });

                            db.collection("users").document(mAuth.getUid()).update("point", FieldValue.increment(point));
                            db.collection("users").document(mAuth.getUid()).collection("posts").document(postid).set(postID);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new HomeFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();

                        } else {
                            placeRef.set(newEatery).addOnCompleteListener(task1 -> {
                                placeRef.update("postNum", FieldValue.increment(1));
                                placeRef.collection("posts").document(postid).set(postID);
                            });
                            db.collection("posts").document(postid).set(newPost).addOnCompleteListener(task1 -> {
                                for (int i = 0; i < mSelected.size(); i++) {
                                    db.collection("posts").document(postid).update("photo_id", FieldValue.arrayUnion(mSelected.get(i).toString()));
                                }
                            });
                            db.collection("users").document(mAuth.getUid()).update("point", FieldValue.increment(point));
                            db.collection("users").document(mAuth.getUid()).collection("posts").document(postid).set(postID);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.fragment_container, new HomeFragment());
                            transaction.addToBackStack(null);
                            transaction.commit();
                        }

                    } else {
                        Log.d("eatery", task.getException().toString());
                    }

                });
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent imageData) {
        super.onActivityResult(requestCode, resultCode, imageData);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(imageData);
            Log.d("Matisse", "mSelected: " + mSelected);
        }
        if (mSelected != null) {
            ImageAdapter imageAdapter = new ImageAdapter(context, mSelected);
            viewPager.setAdapter(imageAdapter);
            imageAdapter.notifyDataSetChanged();
        }
    }
}

