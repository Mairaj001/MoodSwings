package com.example.moodswings.Firebase;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.moodswings.Models.Songs;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class FirebaseRealtimeDb {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    public FirebaseRealtimeDb() {
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
    }

    public void addSongToFavorites(Context context, Songs song, ExecutorService executor) {
        executor.submit(() -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference userFavoritesRef = database.getReference("users").child(userId).child("savedSongs");

                // Check if the song already exists
                userFavoritesRef.orderByChild("title").equalTo(song.getTitle()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Song already exists in favorites
                            Toast.makeText(context, "Song is already saved", Toast.LENGTH_SHORT).show();
                        } else {
                            // Song does not exist, add it to favorites
                            addSongToDatabase(userFavoritesRef, song, context);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("FirebaseRealtimeDB", "Database error: " + databaseError.getMessage());
                        Toast.makeText(context, "Failed to check if song exists", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addSongToDatabase(DatabaseReference userFavoritesRef, Songs song, Context context) {
        // Create a new song entry with a random ID
        String songId = userFavoritesRef.push().getKey();
        if (songId != null) {
            Map<String, Object> songData = new HashMap<>();
            songData.put("title", song.getTitle());
            songData.put("coverImage", song.getCoverImage());
            songData.put("singer", song.getSinger());
            songData.put("songUrl", song.getSongUrl());

            userFavoritesRef.child(songId).setValue(songData)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e("FirebaseRealtimeDB", "Failed to add song to favorites", task.getException());
                                Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    public void getSavedSongs(ExecutorService executor, OnGetSavedSongsListener listener) {
        executor.submit(() -> {
            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                String userId = user.getUid();
                DatabaseReference userFavoritesRef = database.getReference("users")
                        .child(userId)
                        .child("savedSongs");

                userFavoritesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ArrayList<Songs> savedSongs = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Songs song = snapshot.getValue(Songs.class);
                            if (song != null) {
                                savedSongs.add(song);
                            }
                        }
                        listener.onGetSavedSongs(savedSongs);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onCancelled(databaseError.getMessage());
                    }
                });
            } else {
                listener.onGetSavedSongs(new ArrayList<>()); // No user logged in, return empty list
            }
        });
    }



    public interface OnGetSavedSongsListener {
        void onGetSavedSongs(ArrayList<Songs> savedSongs);

        void onCancelled(String errorMessage);
    }




}
