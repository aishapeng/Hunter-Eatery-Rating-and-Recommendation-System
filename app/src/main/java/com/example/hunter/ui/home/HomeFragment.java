package com.example.hunter.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.ui.profile.UserProfileFragment;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private RecyclerView allUsersPostList;
    private PostsRecyclerViewAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String TAG = "posts";
    ArrayList<Posts> allPostsArrayList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_home, container, false);

        allUsersPostList = v.findViewById(R.id.all_users_post_list);
        allUsersPostList.setHasFixedSize(true);
        allUsersPostList.setLayoutManager(new LinearLayoutManager(getActivity()));
        layoutManager = new LinearLayoutManager(getContext());
        allUsersPostList.setLayoutManager(layoutManager);
        adapter = new PostsRecyclerViewAdapter(allPostsArrayList);
        allUsersPostList.setAdapter(adapter);
        adapter.setOnItemClickListener(new PostsRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {

            }

            @Override
            public void OnUsernameClick(int position) {
                String chosenUser=allPostsArrayList.get(position).getUser_id();
                UserProfileFragment userProfileFragment=UserProfileFragment.newInstance(chosenUser);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,userProfileFragment).commit();
            }
        });

        db.collection("posts")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Posts postsList = document.toObject(Posts.class);
                            allPostsArrayList.add(postsList);
//                            name = postsList.getUser_id();
//                            Log.d(TAG, postsList.getUser_id());
//                            Log.d(TAG, document.getId() + " => " + document.getData());
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }

                });

        return v;
    }
}