package com.example.musicplayer;

import java.io.Serializable;

public class Track implements Serializable {
    private String trackLink;
    private String picLink;
    private String title;
    private String artist;

    public Track(String trackLink, String picLink, String title, String artist) {
        this.trackLink = trackLink;
        this.picLink = picLink;
        this.title = title;
        this.artist = artist;
    }

    public String getTrackLink() {
        return trackLink;
    }

    public String getPicLink() {
        return picLink;
    }

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

}
