package com.example.hunter;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SearchingFragment extends Fragment {

    private EditText search;
    private ImageButton searchbtn;
    private RecyclerView resultList;

    private RecyclerView.Adapter<RecyclerView.ViewHolder> adapter;
    final int VIEW_TYPE_USER = 0;
    final int VIEW_TYPE_EATERIES = 1;

    private ArrayList<User> userArrayList = new ArrayList<User>();
    private ArrayList<Eateries> eateriesArrayList = new ArrayList<Eateries>();

    FirebaseFirestore db = FirebaseFirestore.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_searching, container, false);
        //return inflater.inflate(R.layout.fragment_searching, container, false);

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
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        userArrayList = new ArrayList<>();
                        for(DocumentChange document :queryDocumentSnapshots.getDocumentChanges()){
                            userArrayList.add(document.getDocument().toObject(User.class));
                        }
                    }
                });

        db.collection("eateries")
                .orderBy("placeName")
                .startAt(searchText)
                .endAt(searchText + "\uf8ff")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        eateriesArrayList = new ArrayList<>();
                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                            eateriesArrayList.add(doc.getDocument().toObject(Eateries.class));
                        }
                    }
                });

        adapter = new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view;

                if (viewType==VIEW_TYPE_USER){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.user_list_layout,parent,false);
                    return  new SearchingFragment.UserViewHolder(view);
                }
                if(viewType==VIEW_TYPE_EATERIES){
                    view = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.eateries_list_layout,parent,false);
                    return new SearchingFragment.EateriesViewHolder(view);
                }
                return null;

            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                if(holder instanceof SearchingFragment.UserViewHolder){
                    ((SearchingFragment.UserViewHolder) holder).userName.setText(userArrayList.get(position).getUsername());
                    ((SearchingFragment.UserViewHolder) holder).category.setText("User");
                    ((SearchingFragment.UserViewHolder) holder).userImage.setImageURI(null);
                    if(userArrayList.get(position).getPhoto()!=null){
                        Uri uri=Uri.parse(userArrayList.get(position).getPhoto());
                        Glide.with(getContext()).load(uri).into(((SearchingFragment.UserViewHolder) holder).userImage);
                    }
                }

                if(holder instanceof SearchingFragment.EateriesViewHolder){
                    ((SearchingFragment.EateriesViewHolder) holder).placeName.setText(eateriesArrayList.get(position-userArrayList.size()).getPlaceName());
                    ((SearchingFragment.EateriesViewHolder) holder).category.setText("eatery");
                    ((SearchingFragment.EateriesViewHolder) holder).address.setText(Html.fromHtml(eateriesArrayList.get(position-userArrayList.size()).getAddress()));
                    ((SearchingFragment.EateriesViewHolder) holder).eateryImage.setImageURI(null);
                    if(eateriesArrayList.get(position-userArrayList.size()).getPhoto()!=null){
                        List photo = eateriesArrayList.get(position-userArrayList.size()).getPhoto();
                        String pp = photo.get(0).toString();
                        Uri eateryUri = Uri.parse(pp);
                        Glide.with(getContext()).load(eateryUri).into(((SearchingFragment.EateriesViewHolder) holder).eateryImage);
                    }
                }

            }

            @Override
            public int getItemCount() {
                return userArrayList.size()+eateriesArrayList.size();
            }

            @Override
            public int getItemViewType(int position){
                if(position<userArrayList.size()){
                    return VIEW_TYPE_USER;
                }
                if(position-userArrayList.size()<eateriesArrayList.size()){
                    return VIEW_TYPE_EATERIES;
                }
                return -1;
            }
        };

        resultList.setAdapter(adapter);
    }

    private class UserViewHolder extends RecyclerView.ViewHolder {
        TextView userName;
        ImageView userImage;
        TextView category;

        UserViewHolder(View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userImage = itemView.findViewById(R.id.userImage);
            category = itemView.findViewById(R.id.catagory);
        }
    }

    private class EateriesViewHolder extends RecyclerView.ViewHolder{
        TextView placeName;
        TextView category;
        TextView address;
        ImageView eateryImage;

        EateriesViewHolder(View itemView){
            super(itemView);
            placeName = itemView.findViewById(R.id.placeName);
            category = itemView.findViewById(R.id.catagory);
            address = itemView.findViewById(R.id.address);
            eateryImage = itemView.findViewById(R.id.eateryImage);
        }
    }
}
