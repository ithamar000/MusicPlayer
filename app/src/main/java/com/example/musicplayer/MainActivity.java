package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = getSharedPreferences("details", MODE_PRIVATE);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Track> trackList = new ArrayList<>();
        if (sp.contains("firstRunFlag")){
            trackList.add(new Track("https://www.syntax.org.il/xtra/bob.m4a",
            "https://img.discogs.com/zjRP8Xyhtj_QBVmzYyc5I9EQ2Pc=/fit-in/521x523/filters:strip_icc():format(jpeg):mode_rgb():quality(90)/discogs-images/R-4882204-1378373149-1399.jpeg.jpg",
            "One more cup of coffee",
            "Bob Dylan"));
            trackList.add(new Track("https://www.syntax.org.il/xtra/bob1.m4a",
                    "https://images.rapgenius.com/f9fa75848596b53395fe7f6ccd25844c.619x414x1.jpg?w=144",
                    "Sara",
                    "Bob Dylan"));
            trackList.add(new Track("https://www.syntax.org.il/xtra/bob2.mp3",
                    "https://images-na.ssl-images-amazon.com/images/I/71a7yWLeyTL._SL1500_.jpg",
                    "The man in me",
                    "Bob Dylan"));
        }

        TrackAdapter trackAdapter = new TrackAdapter(trackList, this);
        recyclerView.setAdapter(trackAdapter);

    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("firstRunFlag", false);
        editor.commit();
    }
}