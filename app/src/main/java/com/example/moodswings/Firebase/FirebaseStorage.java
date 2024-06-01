package com.example.moodswings.Firebase;

import android.util.Log;

import com.example.moodswings.Models.Songs;
import com.example.moodswings.callBacks.FirestoreCallback;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FirebaseStorage {

    private FirebaseFirestore db;

    public FirebaseStorage(){
        db = FirebaseFirestore.getInstance();
    }

    public void getSongDataNeutral(FirestoreCallback firestoreCallback) {
        db.collection("neutral").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Songs> songList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Songs song = document.toObject(Songs.class);
                            songList.add(song);
                        }
                        firestoreCallback.onCallback(songList);
                    } else {
                        Log.w("FirestoreHelper", "Error getting documents.", task.getException());
                    }
                });
    }

}
