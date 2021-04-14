package com.example.musicplayer;

import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TrackListSingelton {

    private static TrackListSingelton trackListSingelton;
    public ArrayList<Track> trackList;
    public int currentlyPlayingIndex = (-1);
    public TextView currentlyPlayingTV;


    private TrackListSingelton() {

    }

    public static TrackListSingelton getInstance( ) {
        if(trackListSingelton == null) {
            trackListSingelton = new TrackListSingelton();
            trackListSingelton.trackList = new ArrayList<>();
            //trackListSingelton.currentlyPlayingIndex = (-1);
        }
        return trackListSingelton;
    }

}
