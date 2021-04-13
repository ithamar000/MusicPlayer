package com.example.musicplayer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Random;

public class AddTrackFragment extends DialogFragment {

    private EditText mEditText;
    final int CAMERA_REQUEST = 1;
    final int WRITE_PERMISSION_REQUEST = 1;
    File file;


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
        Button okBtn = v.findViewById(R.id.btn_add_song);
        ImageButton picBtn = v.findViewById(R.id.btn_add_pic);
        picBtn.setVisibility(View.GONE); //TODO fix before making visible

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

        picBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23){
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},WRITE_PERMISSION_REQUEST);
                }
                file = new File(Environment.getExternalStorageDirectory(),"pic.jpg");
                Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(MediaStore.EXTRA_OUTPUT,fileUri);
                AddTrackFragment.this.startActivityForResult(intent,CAMERA_REQUEST);
            }
        });

        return v;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==WRITE_PERMISSION_REQUEST){
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),"Unable to take picture without permission", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST){
            EditText trackPicET = getView().findViewById(R.id.ET_pic_link);
            trackPicET.setText(file.getAbsolutePath());

        }
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