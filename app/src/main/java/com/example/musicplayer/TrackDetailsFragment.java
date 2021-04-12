package com.example.musicplayer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;

public class TrackDetailsFragment extends DialogFragment {

    public TrackDetailsFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static TrackDetailsFragment newInstance(String trackTitle, String trackArtist, String trackLink, String picLink) {
        TrackDetailsFragment frag = new TrackDetailsFragment();
        Bundle args = new Bundle();
        args.putString("trackTitle", trackTitle);
        args.putString("trackArtist", trackArtist);
        args.putString("trackLink", trackLink);
        args.putString("picLink", picLink);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.track_details, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        // Fetch arguments from bundle and set title
        String trackTitle = getArguments().getString("trackTitle", "Track name unavailable");
        String trackArtist = getArguments().getString("trackArtist", "Track artist unavailable");
        String picLink = getArguments().getString("picLink", "Track pic unavailable");
        //String trackLink = getArguments().getString("trackLink", "Track link unavailable");


        TextView trackTV = view.findViewById(R.id.TV_track_title); trackTV.setText(trackTitle);
        TextView artistTV = view.findViewById(R.id.TV_artist_name); artistTV.setText(trackArtist);
        ImageView trackPicIV = view.findViewById(R.id.IV_track_pic);
        Glide.with(getContext()).load(picLink).into(trackPicIV);

        getDialog().setTitle("Track Details");
        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}