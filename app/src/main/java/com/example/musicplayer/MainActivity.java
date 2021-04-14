package com.example.musicplayer;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.Fragment;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements AddTrackFragment.onAddEventListener {

    SharedPreferences sp;
    TrackListSingelton trackListSingelton;
    ImageButton playImageButton;
    ImageButton pauseImageButton;
    ImageButton nextImageButton;
    ImageButton prevImageButton;
    ImageButton addSongImageButton;
    TrackAdapter trackAdapter;


    @Override
    public void addEvent(Track track) {
        trackListSingelton.trackList.add(track);
        trackAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("details", MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        trackListSingelton = TrackListSingelton.getInstance();
        trackListSingelton.currentlyPlayingTV = findViewById(R.id.tv_currently_playing);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isMyServiceRunning(MusicService.class)){
                trackListSingelton.currentlyPlayingTV.setText(trackListSingelton.trackList
                        .get(trackListSingelton.currentlyPlayingIndex).getTitle());
                trackListSingelton.currentlyPlayingTV.setVisibility(View.VISIBLE);

            }
        }


        if (!sp.contains("firstRunFlag")) {
            trackListSingelton.trackList.add(new Track("https://www.syntax.org.il/xtra/bob.m4a",
                    "https://img.discogs.com/zjRP8Xyhtj_QBVmzYyc5I9EQ2Pc=/fit-in/521x523/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-4882204-1378373149-1399.jpeg.jpg",
                    "One more cup of coffee",
                    "Bob Dylan"));
            trackListSingelton.trackList.add(new Track("https://www.syntax.org.il/xtra/bob1.m4a",
                    "https://i1.sndcdn.com/artworks-000148133727-rhhdkf-t500x500.jpg",
                    "Sara",
                    "Bob Dylan"));
            trackListSingelton.trackList.add(new Track("https://www.syntax.org.il/xtra/bob2.mp3",
                    "https://images-na.ssl-images-amazon.com/images/I/71a7yWLeyTL._SL1500_.jpg",
                    "The man in me",
                    "Bob Dylan"));
        }
        else {
            try {
                FileInputStream fis = openFileInput("trackList");
                ObjectInputStream ois = new ObjectInputStream(fis);
                trackListSingelton.trackList = (ArrayList<Track>) ois.readObject();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        trackAdapter = new TrackAdapter(trackListSingelton.trackList, this);
        trackAdapter.setListener(new TrackAdapter.MyTrackListener() {
            @Override
            public void onTrackClicked(int position, View view) {
                Track clickedTrack = trackListSingelton.trackList.get(position);

                FragmentManager fm = getSupportFragmentManager();
                TrackDetailsFragment trackDetailsFragment = TrackDetailsFragment.newInstance(clickedTrack.getTitle(),
                        clickedTrack.getArtist(), clickedTrack.getTrackLink(), clickedTrack.getPicLink());
                trackDetailsFragment.show(fm, "fragment_edit_name");
            }

            @Override
            public void onTrackLongClicked(int position, View view) {
            }
        });

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.DOWN | ItemTouchHelper.UP | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

                int fromPosition = viewHolder.getAbsoluteAdapterPosition();
                int toPosition = target.getAbsoluteAdapterPosition();

                Collections.swap(trackListSingelton.trackList, fromPosition, toPosition);

                if(trackListSingelton.currentlyPlayingIndex == fromPosition)
                    trackListSingelton.currentlyPlayingIndex = toPosition;

                if(trackListSingelton.currentlyPlayingIndex == toPosition)
                    trackListSingelton.currentlyPlayingIndex = fromPosition;

                recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);
                if (trackListSingelton.currentlyPlayingIndex != (-1))
                    trackListSingelton.currentlyPlayingIndex = trackListSingelton.trackList
                            .indexOf(trackListSingelton.trackList.get(trackListSingelton.currentlyPlayingIndex));
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.LEFT | direction == ItemTouchHelper.RIGHT) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Delete track")
                            .setMessage("Are you sure you want to delete this track?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    trackListSingelton.trackList.remove(viewHolder.getAbsoluteAdapterPosition());
                                    trackAdapter.notifyItemRemoved(viewHolder.getAbsoluteAdapterPosition());
                                }
                            })
                            .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    trackAdapter.notifyItemChanged(viewHolder.getAbsoluteAdapterPosition());
                                }
                            })
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG)
                    viewHolder.itemView.setAlpha(0.65f);
            }

            @Override
            public void clearView(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                viewHolder.itemView.setAlpha(1.0f);

            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.setAdapter(trackAdapter);

        playImageButton = findViewById(R.id.play_btn);
        pauseImageButton = findViewById(R.id.pause_btn);
        nextImageButton = findViewById(R.id.next_btn);
        prevImageButton = findViewById(R.id.prev_btn);
        addSongImageButton = findViewById(R.id.add_song_btn);



        playImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("command", "new_instance");
                intent.putExtra("trackList", trackListSingelton.trackList);
                startService(intent);
            }
        });

        pauseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("command", "pause");
                startService(intent);
            }
        });

        nextImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("command", "next");
                startService(intent);
            }
        });

        prevImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MusicService.class);
                intent.putExtra("command", "prev");
                startService(intent);
            }
        });

        addSongImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fm = getSupportFragmentManager();
                AddTrackFragment addSongDialogFragment = AddTrackFragment.newInstance("Some Title");
                addSongDialogFragment.show(fm, "fragment_add_song");
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("firstRunFlag", false);
        editor.commit();

        try {
            FileOutputStream fos = openFileOutput("trackList", MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(trackListSingelton.trackList);
            oos.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}