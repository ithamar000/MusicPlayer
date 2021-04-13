package com.example.musicplayer;

import java.util.ArrayList;

public class TrackListSingelton {

    private static TrackListSingelton trackListSingelton;
    public ArrayList<Track> trackList;
    public int currentlyPlayingIndex = (-1);


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
