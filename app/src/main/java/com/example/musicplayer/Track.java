package com.example.musicplayer;

public class Track {
    private String trackLink;
    private String picLink;
    private String title;
    private String artist;
    private String duration;

    public Track(String trackLink, String picLink, String title, String artist, String duration) {
        this.trackLink = trackLink;
        this.picLink = picLink;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
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

    public String getDuration() {
        return duration;
    }
}
