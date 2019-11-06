package com.example.hunter.ui.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.hunter.Eateries;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class PlacesFragment extends Fragment {
    String id;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String TAG="places";
    Toolbar toolbar;
    TextView placeName, address, openHours, phoneNum, category;
    RatingBar ratingBar;
    Eateries eateries;

    private OnFragmentInteractionListener mListener;

    public PlacesFragment() {
        // Required empty public constructor
    }


    public static PlacesFragment newInstance(String id) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        args.putString("id",id);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id=getArguments().getString("id");

            db.collection("eateries").document(id)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                eateries=document.toObject(Eateries.class);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_places, container, false);
        placeName=v.findViewById(R.id.namePlace);
        address=v.findViewById(R.id.addressPlace);
        openHours=v.findViewById(R.id.openPlace);
        phoneNum=v.findViewById(R.id.phonePlace);
        category=v.findViewById(R.id.categoryPlace);
        toolbar=v.findViewById(R.id.toolbar3);
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_black_24dp);
        toolbar.setNavigationOnClickListener(v1 ->
                getActivity().onBackPressed());
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
