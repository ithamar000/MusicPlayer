package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    final int NOTIF_ID = 1;

    TrackListSingelton trackListSingelton;
    MediaPlayer mediaPlayer = new MediaPlayer();
    RemoteViews remoteViews;
    NotificationManager manager;
    NotificationCompat.Builder builder;

    private void updateNotification(){

        int api = Build.VERSION.SDK_INT;
        if(trackListSingelton.currentlyPlayingIndex != (-1))
            remoteViews.setTextViewText(R.id.song_title,trackListSingelton.trackList
                    .get(trackListSingelton.currentlyPlayingIndex).getTitle() );

        // update the notification
        if (api < Build.VERSION_CODES.HONEYCOMB) {
            manager.notify(NOTIF_ID, builder.build());
        }else if (api >= Build.VERSION_CODES.HONEYCOMB) {
            manager.notify(NOTIF_ID, builder.build());
        }
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.reset();

        trackListSingelton = TrackListSingelton.getInstance();

        String channelID = "channel_id";
        String channelName = "music_channel";

        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        builder =  new NotificationCompat.Builder(this,channelID).setPriority(NotificationCompat.PRIORITY_LOW);

        remoteViews = new RemoteViews(getPackageName(), R.layout.music_notif);


        Intent playIntent = new Intent(this, MusicService.class);
        playIntent.putExtra("command", "play");
        PendingIntent playPendingIntent = PendingIntent.getService(this, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.play_btn, playPendingIntent);

        Intent pauseIntent = new Intent(this, MusicService.class);
        pauseIntent.putExtra("command", "pause");
        PendingIntent pausePendingIntent = PendingIntent.getService(this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.pause_btn, pausePendingIntent);

        Intent nextIntent = new Intent(this, MusicService.class);
        nextIntent.putExtra("command", "next");
        PendingIntent nextPendingIntent = PendingIntent.getService(this, 2, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.next_btn, nextPendingIntent);

        Intent prevIntent = new Intent(this, MusicService.class);
        prevIntent.putExtra("command", "prev");
        PendingIntent prevPendingIntent = PendingIntent.getService(this, 3, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.prev_btn, prevPendingIntent);

        Intent closeIntent = new Intent(this, MusicService.class);
        closeIntent.putExtra("command", "close");
        PendingIntent closePendingIntent = PendingIntent.getService(this, 4, closeIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.close_btn, closePendingIntent);


        builder.setCustomContentView(remoteViews);
        builder.setSmallIcon(android.R.drawable.ic_media_play);

        startForeground(NOTIF_ID, builder.build());
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        playSong(true);
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        trackListSingelton.currentlyPlayingTV.setText(trackListSingelton.trackList
                .get(trackListSingelton.currentlyPlayingIndex).getTitle());
        trackListSingelton.currentlyPlayingTV.setVisibility(View.VISIBLE);
        updateNotification();

        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");

        switch (command){
            case ("new_instance"):
                if(!mediaPlayer.isPlaying()){

                    try{
                        if(trackListSingelton.currentlyPlayingIndex == (-1)) {
                            if (trackListSingelton.trackList.size() > 0) {
                                mediaPlayer.setDataSource(trackListSingelton.trackList.get(0).getTrackLink());
                                trackListSingelton.currentlyPlayingIndex = 0;
                                mediaPlayer.prepareAsync();
                            }
                        }
                        else
                            mediaPlayer.start();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ("play"):
                if (!mediaPlayer.isPlaying())
                    mediaPlayer.start();
                break;
            case ("pause"):
                if (mediaPlayer.isPlaying())
                    mediaPlayer.pause();
                break;
            case ("next"):
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(true);
                break;
            case ("prev"):
                if(mediaPlayer.isPlaying())
                    mediaPlayer.stop();
                playSong(false);
                break;
            case ("close"):
                stopSelf();
                break;
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void playSong(boolean isNext)  {

        if(isNext) {
            trackListSingelton.currentlyPlayingIndex++;
            if (trackListSingelton.currentlyPlayingIndex == trackListSingelton.trackList.size())
                trackListSingelton.currentlyPlayingIndex = 0;
        }
        else {
            trackListSingelton.currentlyPlayingIndex--;
            if(trackListSingelton.currentlyPlayingIndex < 0)
                trackListSingelton.currentlyPlayingIndex = trackListSingelton.trackList.size() - 1;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(trackListSingelton.trackList.get(trackListSingelton.currentlyPlayingIndex).getTrackLink());
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer!= null){
            if(mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
