package com.example.moodswings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;

import android.os.Bundle;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.moodswings.Exoplayer.MyExoplayer;
import com.example.moodswings.Firebase.FirebaseRealtimeDb;
import com.example.moodswings.Models.Songs;
import com.example.moodswings.databinding.ActivityPlayerBinding;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ActivityPlayer extends AppCompatActivity {

    private ActivityPlayerBinding binding;
    private ExoPlayer exoPlayer;

    TextView savedSongBtn;

    FirebaseRealtimeDb firebaseRealtimeDB;

    ExecutorService executorService;

    private final Player.Listener playerListener = new Player.Listener() {
        @Override
        public void onIsPlayingChanged(boolean isPlaying) {
            showGif(isPlaying);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        savedSongBtn=findViewById(R.id.saveSongBtn);




        Songs currentSong = MyExoplayer.getCurrentSong();
        firebaseRealtimeDB = new FirebaseRealtimeDb();
        executorService = Executors.newSingleThreadExecutor();

        savedSongBtn.setOnClickListener(v -> {
            if (currentSong != null) {
                firebaseRealtimeDB.addSongToFavorites(getApplicationContext(), currentSong, executorService);
            } else {
                // Handle case where currentSong is null
                Toast.makeText(getApplicationContext(),"No song is currently playing",Toast.LENGTH_SHORT).show();
            }
        });


        if (currentSong != null) {
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
            binding.playerView.showController(); // Always show the controller
            exoPlayer.addListener(playerListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (exoPlayer != null) {
            exoPlayer.removeListener(playerListener);
            exoPlayer.stop();
        }
        executorService.shutdown();
    }

    private void showGif(boolean show) {
        if (show) {
            binding.songGifImageView.setVisibility(View.VISIBLE);
        } else {
            binding.songGifImageView.setVisibility(View.INVISIBLE);
        }
    }
}
