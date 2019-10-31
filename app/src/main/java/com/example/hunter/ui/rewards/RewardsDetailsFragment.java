package com.example.hunter.ui.rewards;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.hunter.Image;
import com.example.hunter.R;
import com.example.hunter.RewardsItem;
import com.example.hunter.ui.search.PlacesFragment;

import java.text.SimpleDateFormat;
import java.util.Date;


public class RewardsDetailsFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    RewardsItem rewardsItem;
    ImageView imageView;
    TextView placeName;
    TextView title;
    TextView desc;
    TextView points;
    TextView terms;
    TextView availableUntil;

    public RewardsDetailsFragment() {
        // Required empty public constructor
    }


    public static RewardsDetailsFragment newInstance(RewardsItem rewardsItem) {
        RewardsDetailsFragment fragment = new RewardsDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable("rewardsItem",rewardsItem);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            rewardsItem=getArguments().getParcelable("rewardsItem");
            Log.d("rewardsfrag",rewardsItem.getImages());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v= inflater.inflate(R.layout.fragment_rewards_details, container, false);
        imageView=v.findViewById(R.id.image);
        placeName=v.findViewById(R.id.placeName);
        title=v.findViewById(R.id.title);
        desc=v.findViewById(R.id.description);
        points=v.findViewById(R.id.points);
        terms=v.findViewById(R.id.terms);
        availableUntil=v.findViewById(R.id.availableUntil);

        placeName.setText(rewardsItem.getPlaceName());
        title.setText(rewardsItem.getTitle());
        desc.setText(rewardsItem.getDesc());
        points.setText("Points Needed:\t"+rewardsItem.getPoints());
        terms.setText(rewardsItem.getTerms());
        SimpleDateFormat curFormater = new SimpleDateFormat("dd/MM/yyyy");
        Date date=rewardsItem.getAvailableUntil();
        String date2 = curFormater.format(date);
        availableUntil.setText("Available Until:\t"+date2);
        Glide.with(v.getContext()).load(Uri.parse(rewardsItem.getImages())).into(imageView);

        return v;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.placeName:
                PlacesFragment placesFragment=PlacesFragment.newInstance(rewardsItem.getPlaceID());
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,placesFragment).commit();
                break;

        }
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
