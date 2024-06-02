package com.example.moodswings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.moodswings.Adapters.SongAdapter;
import com.example.moodswings.Exoplayer.MyExoplayer;
import com.example.moodswings.Firebase.FirebaseStorage;
import com.example.moodswings.Models.Songs;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ShowSongs extends AppCompatActivity {

    String sentiment;
    LottieAnimationView emojiIcon;

    public final  String TAG="ShowSongs";

    RecyclerView rcv;
    SongAdapter adapter;

    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_songs);

        Intent intent=getIntent();
        sentiment=intent.getStringExtra("Sentiment");
        emojiIcon=findViewById(R.id.cover_image_view);
        rcv=findViewById(R.id.songs_list_recycler_view);

        rcv.setLayoutManager(new LinearLayoutManager(this));
        firebaseStorage = new FirebaseStorage(FirebaseFirestore.getInstance());
        showSentimentIcon(sentiment);

        showRecyclerContent("neutral");

    }

    private void showSentimentIcon(String sentiment) {
        // Assuming you have an emojiIcon that shows different animations based on sentiment
        int animationResId = R.raw.neutral_emoji; // default to neutral

        if (sentiment.equalsIgnoreCase("positive")) {
            animationResId = R.raw.happy_emoji;
        } else if (sentiment.equalsIgnoreCase("negative")) {
            animationResId = R.raw.angry_emoji;
        }

        emojiIcon.setAnimation(animationResId);
        emojiIcon.playAnimation();
    }

    private void showRecyclerContent(String sentiment) {

        firebaseStorage.getSongData(sentiment,   new FirebaseStorage.FirestoreCallback() {
            @Override
            public void onCallback(ArrayList<Songs> songsList) {

                if (songsList.isEmpty()) {
                    Toast.makeText(ShowSongs.this, "No songs found for " + sentiment, Toast.LENGTH_SHORT).show();
                } else {
                    for (Songs song : songsList) {
                        Log.d(TAG, "Song Title: " + song.getTitle()+song.getSongUrl());
                    }
                    runOnUiThread(() -> {

                        adapter = new SongAdapter(ShowSongs.this,songsList );
                        rcv.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(ShowSongs.this, "Error fetching songs: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error fetching songs: " + e.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        MyExoplayer.releasePlayer();
        super.onBackPressed();
    }
}