package com.example.moodswings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.moodswings.Firebase.FirebaseStorage;
import com.example.moodswings.Models.Songs;
import com.example.moodswings.callBacks.FirestoreCallback;

import java.util.List;

public class ShowSongs extends AppCompatActivity {

    String sentiment;
    LottieAnimationView emojiIcon;

    public final  String TAG="ShowSongs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_songs);
        Intent intent=getIntent();
        sentiment=intent.getStringExtra("Sentiment");
        emojiIcon=findViewById(R.id.cover_image_view);

        showRecyclerContent(sentiment);
        showSentimentIcon(sentiment);
    }

    private  void showSentimentIcon(String sentiments){
        if(sentiments.toLowerCase().equals("neutral")){
            emojiIcon.setAnimation(R.raw.neutral_emoji);
            emojiIcon.playAnimation();
        } else if(sentiments.toLowerCase().equals("positive")){
            emojiIcon.setAnimation(R.raw.happy_emoji);
            emojiIcon.playAnimation();
        } else if(sentiments.toLowerCase().equals("negative")){
            emojiIcon.setAnimation(R.raw.angry_emoji);
            emojiIcon.playAnimation();
        }
    }
    private  void showRecyclerContent(String sentiments){
//        if(sentiments)

        FirebaseStorage firestoreHelper = new FirebaseStorage();

        // Call getSongDataNeutral and handle the callback
        firestoreHelper.getSongDataNeutral(new FirestoreCallback() {
            @Override
            public void onCallback(List<Songs> songList) {
                if (songList != null) {
                    // Process the songList here
                    for (Songs song : songList) {
                        Log.d(TAG, "Song Title: " + song.getTitle());
                        // You can process other fields as well (e.g., singer, coverImage, songUrl)
                    }
                } else {
                    Log.d(TAG, "Failed to retrieve song list.");
                }
            }
        });
    }
}