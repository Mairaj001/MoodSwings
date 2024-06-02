package com.example.moodswings;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.media3.exoplayer.ExoPlayer.Builder;
import androidx.media3.ui.PlayerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.moodswings.Exoplayer.MyExoplayer;
import com.example.moodswings.Models.Songs;
import com.example.moodswings.databinding.ActivityPlayerBinding;

public class ActivityPlayer extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            Player.Listener.super.onIsPlayingChanged(isPlaying);
            showGif(isPlaying);
        }
    };

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Songs currentSong = MyExoplayer.getCurrentSong();
        if (currentSong != null) {
            Log.d("ActivityPlayer", "startPlaying: Song is already playing"+currentSong.getTitle()+" "+currentSong.getSongUrl());

            binding.songTitleTextView.setText(currentSong.getTitle());
            binding.songSubtitleTextView.setText(currentSong.getSinger());
            Glide.with(binding.songCoverImageView)
                    .load(currentSong.getCoverImage())
                    .apply(new RequestOptions().circleCrop())
                    .into(binding.songCoverImageView);
            Glide.with(binding.songGifImageView)
                    .load(R.drawable.media_playing)
                    .apply(new RequestOptions().circleCrop())
                    .into(binding.songGifImageView);

            exoPlayer = MyExoplayer.getInstance(this);
            binding.playerView.setPlayer(exoPlayer);
            binding.playerView.showController();
            exoPlayer.addListener(playerListener);
//            MyExoplayer.startPlaying(getApplicationContext(),currentSong);
        } else {
            Toast.makeText(getApplicationContext(),"Current song is null",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.removeListener(playerListener);
            exoPlayer.release();
        }
    }

    private void showGif(boolean show) {
        if (show) {
            binding.songGifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.songGifImageView.setVisibility(View.INVISIBLE);
        }
    }
}
