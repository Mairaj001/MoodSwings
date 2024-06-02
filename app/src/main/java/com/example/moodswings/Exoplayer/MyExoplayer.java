package com.example.moodswings.Exoplayer;

import android.content.Context;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ExoPlayer.Builder;

import com.example.moodswings.Models.Songs;

public class MyExoplayer {

    private static ExoPlayer exoPlayer;
    private static Songs currentSong;

    public static Songs getCurrentSong() {
        return currentSong;
    }

    public static ExoPlayer getInstance(Context context) {
        if (exoPlayer == null) {
            exoPlayer = new Builder(context).build();
        }
        return exoPlayer;
    }

    public static void startPlaying(Context context, Songs song) {
        Log.d("MyExoplayer", "startPlaying: Song: " + song.getTitle()+song.getSongUrl());
        if (exoPlayer == null) {
            exoPlayer = new Builder(context).build();
        }

        // Check if the current song is the same as the selected song
        if (currentSong != null && currentSong.equals(song)) {
            // If it's the same song and it's already playing, do nothing
            if (exoPlayer.isPlaying()) {
                Log.d("MyExoplayer", "startPlaying: Song is already playing");
                return;
            }
        } else {
            exoPlayer.stop(); // Stop the current song
        }

        // It's a new song, so start playing
        currentSong = song;

        if (currentSong != null && currentSong.getSongUrl() != null) {
            MediaItem mediaItem = MediaItem.fromUri(currentSong.getSongUrl());
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
            Log.d("MyExoplayer", "startPlaying: Starting playback for song: " + song.getTitle());
        }
    }

    public static void releasePlayer() {
        if (exoPlayer != null) {
            exoPlayer.release();
            exoPlayer = null;
            currentSong = null;
        }
    }
}
