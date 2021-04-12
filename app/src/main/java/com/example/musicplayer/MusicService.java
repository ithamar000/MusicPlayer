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
import android.widget.RemoteViews;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.util.ArrayList;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    final int NOTIF_ID = 1;

    ArrayList<Track> trackList;
    int currentPlaying = (-1);
    MediaPlayer mediaPlayer = new MediaPlayer();


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

        ArrayList<Track> trackList = new ArrayList<Track>();

        String channelID = "channel_id";
        String channelName = "music_channel";

        NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_LOW);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder =  new NotificationCompat.Builder(this,channelID).setPriority(NotificationCompat.PRIORITY_LOW);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.music_notif);


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
        mediaPlayer.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String command = intent.getStringExtra("command");

        switch (command){
            case ("new_instance"):
                if(!mediaPlayer.isPlaying()){


                    trackList = (ArrayList<Track>) intent.getSerializableExtra("trackList");
                    try{
                        if(currentPlaying == (-1)) {
                            if (trackList.size() > 0) {
                                mediaPlayer.setDataSource(trackList.get(0).getTrackLink());
                                currentPlaying = 0;
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
            currentPlaying++;
            if (currentPlaying == trackList.size())
                currentPlaying = 0;
        }
        else {
            currentPlaying--;
            if(currentPlaying < 0)
                currentPlaying = trackList.size() - 1;
        }
        mediaPlayer.reset();
        try {
            mediaPlayer.setDataSource(trackList.get(currentPlaying).getTrackLink());
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
