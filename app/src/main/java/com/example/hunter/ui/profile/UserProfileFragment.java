package com.example.hunter.ui.profile;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hunter.R;
import com.example.hunter.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;


public class UserProfileFragment extends Fragment {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CircleImageView proPic;
    private TextView username, postNum, followNum, followingNum;
    private Button follow;
    private RecyclerView posts;
    private User user;
    private Context context;

    private OnFragmentInteractionListener mListener;

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String param1) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString("userID",param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String userId=getArguments().getString("userID");
            db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    DocumentSnapshot documentSnapshot =task.getResult();
                    user=documentSnapshot.toObject(User.class);
                    setProperties();
                }
            });
        }
    }

    public void setProperties(){
        Glide.with(context).load(user.getPhoto()).into(proPic);
        username.setText(user.getUsername());
        if(user.getPosts()!=null && user.getFollowers()!=null && user.getFollowing()!=null ){
            postNum.setText(user.getPosts().size());
            followNum.setText(user.getFollowers().size());
            followingNum.setText(user.getFollowing().size());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_user_profile, container, false);

        proPic=v.findViewById(R.id.profilePicUser);
        username=v.findViewById(R.id.usernameProfileUser);
        postNum=v.findViewById(R.id.postNumCountUser);
        followNum=v.findViewById(R.id.followerNumCountUser);
        followingNum=v.findViewById(R.id.followingNumCountUser);
        follow=v.findViewById(R.id.followButton);
        posts=v.findViewById(R.id.userPostsUser);
        context=v.getContext();

        return v;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
