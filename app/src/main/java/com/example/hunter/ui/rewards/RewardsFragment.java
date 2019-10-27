package com.example.hunter.ui.rewards;

import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.hunter.R;
import com.example.hunter.RewardsItem;
import com.example.hunter.RewardsRecyclerViewAdapter;
import com.example.hunter.SpacesItemDecoration;
import com.example.hunter.ui.home.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class RewardsFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "rewards";
    private RecyclerView recyclerView;
    private RewardsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    ArrayList<RewardsItem> rewardsItemArrayList = new ArrayList<>();
    private String name;


    public RewardsFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rewards, container, false);

        recyclerView = v.findViewById(R.id.rewards_rv);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RewardsRecyclerViewAdapter(rewardsItemArrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new SpacesItemDecoration(40));

        db.collection("rewards")
                .get()
                .addOnCompleteListener( task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    RewardsItem rewardsItem = document.toObject(RewardsItem.class);
                    rewardsItemArrayList.add(rewardsItem);
                    name = rewardsItem.getPlaceName();
                    Log.d(TAG, rewardsItem.getPlaceName());
                    Log.d(TAG, document.getId() + " => " + document.getData());
                    adapter.notifyDataSetChanged();
                }
            } else {
                Log.d(TAG, "Error getting documents: ", task.getException());
            }

        });

        adapter.setOnItemClickListener(position -> {
            RewardsItem currentItem=rewardsItemArrayList.get(position);
//            Bundle args = new Bundle();
//            args.putParcelable("rewardsItem",currentItem);
            RewardsDetailsFragment rewardsDetailsFragment=RewardsDetailsFragment.newInstance(currentItem);
//            rewardsDetailsFragment.setArguments(args);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,rewardsDetailsFragment).commit();
        });


        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
