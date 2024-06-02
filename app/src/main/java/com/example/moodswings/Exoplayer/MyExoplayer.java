package com.example.moodswings.Exoplayer;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import androidx.media3.common.MediaItem;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ExoPlayer.Builder;

import com.example.moodswings.Models.Songs;

public class MyExoplayer {

    private static final String TAG = "MyExoplayer";

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

    public static void SetCurrentSong(Songs songs){
        currentSong=songs;
    }
    public static void startPlaying(Context context, Songs song) {
        Log.d(TAG, "startPlaying: Song: " + song.getTitle()+""+song.getSongUrl());

        // Ensure ExoPlayer instance is initialized
        if (exoPlayer == null) {
            exoPlayer = new Builder(context).build();
        }

        // Check if the current song is the same as the selected song
        if (currentSong != null && currentSong.equals(song)) {
            // If it's the same song and it's already playing, do nothing
            if (exoPlayer.isPlaying()) {
                Log.d(TAG, "startPlaying: Song is already playing");
                return;
            }
        }

        // It's a new song, so start playing
        currentSong = song;

        // Prepare and play the new song
        if (currentSong != null && currentSong.getSongUrl() != null) {
            MediaItem mediaItem = MediaItem.fromUri(Uri.parse(currentSong.getSongUrl()));
            exoPlayer.setMediaItem(mediaItem);
            exoPlayer.prepare();
            exoPlayer.play();
            Log.d(TAG, "startPlaying: Starting playback for song: " + song.getTitle());
        } else {
            Log.e(TAG, "startPlaying: Song URL is null or empty");
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
