package com.example.moodswings.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.example.moodswings.ActivityPlayer;
import com.example.moodswings.Exoplayer.MyExoplayer;
import com.example.moodswings.Models.Songs;
import com.example.moodswings.R;

import java.util.ArrayList;

public class SavedSongAdapter extends RecyclerView.Adapter<SavedSongAdapter.SavedSongViewHolder> {


    Context context;
    ArrayList<Songs> songsArrayList;
    public SavedSongAdapter(Context context, ArrayList<Songs> songs){
        this.context=context;
        this.songsArrayList=songs;
    }


    @NonNull
    @Override
    public SavedSongAdapter.SavedSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_songs_recycler, parent, false);
        return new SavedSongAdapter.SavedSongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedSongViewHolder holder, int position) {

        Songs song = songsArrayList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.singer.setText(song.getSinger());
        RequestOptions requestOptions = new RequestOptions()
                .transform(new CenterCrop(), new RoundedCorners(12))
                .diskCacheStrategy(DiskCacheStrategy.ALL);

        Glide.with(context)
                .load(song.getCoverImage())
                .apply(requestOptions)
                .into(holder.coverImage);



    }



    @Override
    public int getItemCount() {
        return songsArrayList.size();
    }

    public static class SavedSongViewHolder extends RecyclerView.ViewHolder{

        TextView songTitle;
        TextView singer,deletebtn;
        ImageView coverImage;

        public SavedSongViewHolder(@NonNull View itemView) {
            super(itemView);

            songTitle = itemView.findViewById(R.id.cardTitle);
            singer = itemView.findViewById(R.id.cardSinger);
            coverImage = itemView.findViewById(R.id.cardImage);
            deletebtn=itemView.findViewById(R.id.removeBtn);

        }
    }
}
