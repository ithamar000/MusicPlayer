package com.example.musicplayer;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class AddTrackFragment extends DialogFragment {

    private EditText mEditText;

    public interface onAddEventListener {
        public void addEvent(Track track);
    }

    onAddEventListener addEventListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            addEventListener = (onAddEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    public AddTrackFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static AddTrackFragment newInstance(String title) {
        AddTrackFragment frag = new AddTrackFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.add_track, container);

        EditText trackTitleET = v.findViewById(R.id.ET_title);
        EditText trackArtistET = v.findViewById(R.id.ET_artist_name);
        EditText trackPicET = v.findViewById(R.id.ET_pic_link);
        EditText trackLinkET = v.findViewById(R.id.ET_track_link);
        Button okBtn = (Button) v.findViewById(R.id.btn_add_song);

        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String trackName = trackTitleET.getText().toString();
                String trackArtist = trackArtistET.getText().toString();
                String trackPicLink = trackPicET.getText().toString();
                String trackLink = trackLinkET.getText().toString();

                addEventListener.addEvent(new Track(trackLink, trackPicLink, trackName, trackArtist));
                dismiss();
            }
        });

        //TODO ADD CAMERA FUNCTION
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get field from view
        mEditText = (EditText) view.findViewById(R.id.ET_title);
        // Fetch arguments from bundle and set title
        String title = getArguments().getString("title", "Enter Name");
        getDialog().setTitle(title);
        // Show soft keyboard automatically and request focus to field
        mEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }
}