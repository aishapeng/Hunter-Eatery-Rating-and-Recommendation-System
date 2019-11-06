package com.example.hunter.ui.home;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.hunter.ImageAdapter;
import com.example.hunter.Posts;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostsRecyclerViewAdapter extends RecyclerView.Adapter<PostsRecyclerViewAdapter.RecyclerViewHolder> {
//    private ArrayList<com.example.hunter.Image> mDataset;
//    private FeedActivity mActivity;

    private ArrayList<Posts> postsList;
    private Context context;
    private OnItemClickListener mListener;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

//    private CompoundButton.OnCheckedChangeListener mListener;

//    public interface OnCheckedChangeListener
//    {
//        void OnItemChecked(int position);
//    }

    public interface OnItemClickListener {
        void OnItemClick(int position);

        void OnUsernameClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView postProfileImage;
        public TextView postProfileUsername;
        public TextView postPlaceId;
        public RatingBar postRatingBar;
        private RatingBar priceRateBar;
        public TextView postDateCreated;
        public ViewPager postImage;
        public CheckBox postLikeIcon;
        public TextView likesCount;
        public TextView likesText;
        public TextView postDescription;
        public TextView postComments;


        public RecyclerViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);
            postProfileImage = itemView.findViewById(R.id.post_profile_image);
            postProfileUsername = itemView.findViewById(R.id.post_profile_username);
            postPlaceId = itemView.findViewById(R.id.post_place_id);
            postRatingBar = itemView.findViewById(R.id.post_rating_bar);
            postDateCreated = itemView.findViewById(R.id.post_date_created);
            postImage = itemView.findViewById(R.id.post_image);
            postLikeIcon = itemView.findViewById(R.id.post_like_icon);
            likesCount = itemView.findViewById(R.id.likesCount);
            likesText = itemView.findViewById(R.id.likesText);
            postDescription = itemView.findViewById(R.id.post_description);
            postComments = itemView.findViewById(R.id.comments);
            priceRateBar = itemView.findViewById(R.id.priceRateBar);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnItemClick(position);
                    }
                }
            });

            postProfileUsername.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.OnUsernameClick(position);
                    }
                }
            });
        }
    }

    public PostsRecyclerViewAdapter(ArrayList<Posts> postsList) {
        this.postsList = postsList;

    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_users_post, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(v, mListener);
        context = parent.getContext();
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostsRecyclerViewAdapter.RecyclerViewHolder holder, int position) {
        Posts currentPost = postsList.get(position);
        db.collection("users").document(currentPost.getUser_id()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists()) {
                    User user = documentSnapshot.toObject(User.class);

                    holder.postProfileImage.setScaleType(CircleImageView.ScaleType.CENTER_CROP);
                    Glide.with(context).load(user.getPhoto()).into(holder.postProfileImage);

                    db.collection("posts").document(currentPost.getPostId()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot1 = task1.getResult();
                            List<String> photoID = (List<String>) documentSnapshot1.get("photo_id");
                            List<Uri> photoUri = new ArrayList<>();
                            for (int i = 0; i < photoID.size(); i++) {
                                photoUri.add(Uri.parse(photoID.get(i)));
                            }
                            ImageAdapter imageAdapter = new ImageAdapter(context, photoUri);
                            holder.postImage.setAdapter(imageAdapter);
                            imageAdapter.notifyDataSetChanged();
                        }
                    });

                    holder.postProfileUsername.setText(user.getDisplayName());
                    db.collection("eateries").document(currentPost.getPlace_id()).get().addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            DocumentSnapshot documentSnapshot1 = task1.getResult();
                            String placeName = documentSnapshot1.get("placeName").toString();
                            holder.postPlaceId.setText(placeName);
                        }
                    });
                    holder.postRatingBar.setRating(Float.parseFloat(currentPost.getRating().toString()));
                    holder.priceRateBar.setRating(Float.parseFloat(currentPost.getPriceLvl().toString()));
                    String dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(currentPost.getDate_created());
                    holder.postDateCreated.setText(dateFormat);
                    holder.postDescription.setText(currentPost.getCaption());
                    holder.likesCount.setText("0");
                    holder.likesText.setText("Likes");
                    holder.likesCount.setClickable(true);
//                holder.postComments.setClickable(true);
                }

            }

        });

    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }
}
