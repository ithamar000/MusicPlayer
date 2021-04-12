package com.example.musicplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;

import java.util.List;

public class TrackAdapter extends RecyclerView.Adapter<TrackAdapter.TrackViewHolder> {

    private List<Track> trackList;
    private Context context;
    private MyTrackListener listener;

    public TrackAdapter(List<Track> trackList, Context context) {
        this.trackList = trackList;
        this.context = context;
    }

    interface MyTrackListener{
        void onTrackClicked(int position, View view);
        void onTrackLongClicked(int position, View view);
    }

    public void setListener(MyTrackListener listener){
        this.listener = listener;
    }

    public class TrackViewHolder extends RecyclerView.ViewHolder{
        ImageView trackImageIv;
        TextView trackTitleTv;
        TextView trackArtistTv;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);

            trackImageIv = itemView.findViewById(R.id.track_cell_image);
            trackTitleTv = itemView.findViewById(R.id.track_cell_title);
            trackArtistTv = itemView.findViewById(R.id.track_cell_artist);
            trackTitleTv.setSelected(true);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onTrackClicked(getAbsoluteAdapterPosition(),v);
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (listener != null)
                        listener.onTrackLongClicked(getAbsoluteAdapterPosition(),v);
                    return false;
                }
            });
        }
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.track_cell,parent,false);
        TrackViewHolder trackViewHolder = new TrackViewHolder(view);
        return trackViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {

        Track track = trackList.get(position);
        holder.trackArtistTv.setText(track.getArtist());
        holder.trackTitleTv.setText(track.getTitle());
        Glide.with(context).load(track.getPicLink()).into(holder.trackImageIv);
    }


    @Override
    public int getItemCount() {
        return trackList.size();
    }
}
