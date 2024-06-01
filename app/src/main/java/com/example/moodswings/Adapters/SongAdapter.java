package com.example.moodswings.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.moodswings.Models.Songs;
import com.example.moodswings.R;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> {

    Context context;
    ArrayList<Songs> songsArrayList;
    public SongAdapter(Context context,ArrayList<Songs> songsArrayList) {
         this.context=context;
         this.songsArrayList=songsArrayList;
    }


    @NonNull
    @Override
    public SongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.song_list_recycler_view, parent, false);
        return new SongViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SongViewHolder holder, int position) {
        Songs song = songsArrayList.get(position);
        holder.songTitle.setText(song.getTitle());
        holder.singer.setText(song.getSinger());
        Glide.with(context).load(song.getCoverImage()).into(holder.coverImage);
    }

    @Override
    public int getItemCount() {
        return songsArrayList.size();
    }





    public static class SongViewHolder extends RecyclerView.ViewHolder {
        TextView songTitle;
        TextView singer;
        ImageView coverImage;

        public SongViewHolder(@NonNull View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.song_title_text_view);
            singer = itemView.findViewById(R.id.song_subtitle_text_view);
            coverImage = itemView.findViewById(R.id.song_cover_image_view);
        }
    }
}
