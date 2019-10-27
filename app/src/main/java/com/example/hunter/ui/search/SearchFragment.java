package com.example.hunter.ui.search;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class SearchFragment extends Fragment  {


    private EditText search;
    private ImageButton searchbtn;
    private RecyclerView resultList;

    private RecyclerView.Adapter<UserViewHolder> adapter;
    private ArrayList<User> userArrayList = new ArrayList<User>();

    //private FirestoreRecyclerAdapter<User, UserViewHolder> adapter;

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_search, container, false);

        search = root.findViewById(R.id.searchEditText);
        searchbtn = root.findViewById(R.id.searchButton);
        resultList = root.findViewById(R.id.resultList);
        resultList.setHasFixedSize(true);
        resultList.setLayoutManager(new LinearLayoutManager(getActivity()));

        searchbtn.setOnClickListener(v -> {
            String searchText = search.getText().toString();
            FirestoreUserSearch(searchText);
        });
        return root;
    }


    private void FirestoreUserSearch(String searchText) {

        db.collection("users")
                .orderBy("username")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            userArrayList = new ArrayList<>();
                            userArrayList.add(document.toObject(User.class));
                        }
                    } else {
                        Log.w("TAG", "Error getting documents.", task.getException());
                    }
                });



        adapter = new RecyclerView.Adapter<UserViewHolder>() {
            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list_layout, parent, false);
                return new UserViewHolder(view);
            }

            @Override
            public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
                holder.userName.setText(userArrayList.get(position).getUsername());
                holder.userImage.setImageURI(null);
                if(userArrayList.get(position).getPhoto()!=null){
                    Uri uri=Uri.parse(userArrayList.get(position).getPhoto());
                    Glide.with(getContext()).load(uri).into(holder.userImage);
                }
            }

            @Override
            public int getItemCount() {
                return userArrayList.size();
            }
        };


       /* Query query = db.collection("users");
        query.orderBy("uid")
                .startAt(searchText).endAt(searchText+"\uf8ff");


        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<User, UserViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull UserViewHolder userViewHolder, int i, @NonNull User user) {
                userViewHolder.setDetails(getActivity().getApplicationContext(),
                        user.getUsername(), user.getPhoto());
            }

            @NonNull
            @Override
            public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.user_list_layout, parent, false);
                return new UserViewHolder(view);
            }
        };*/
        resultList.setAdapter(adapter);
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImage;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
        }
        /*public void setDetails(Context ctx, String name, String image){
            TextView userName = view.findViewById(R.id.userName);
            ImageView userImage = view.findViewById(R.id.userImage);

            userName.setText(name);

            userImage.setImageURI(null);
            if(image!=null){
                Uri uri=Uri.parse(image);
                Glide.with(ctx).load(uri).into(userImage);
            }
        }*/
    }

}