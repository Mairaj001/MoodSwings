package com.example.moodswings.Firebase;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.example.moodswings.Models.Songs;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import android.util.Log;

public class FirebaseStorage {

    private static final String TAG = "";
    private FirebaseFirestore db;
    private Executor executor = Executors.newSingleThreadExecutor();

    public FirebaseStorage(FirebaseFirestore db) {
        this.db = db;
    }

    public interface FirestoreCallback {
        void onCallback(ArrayList<Songs> songsList);
        void onFailure(Exception e);
    }
    public void getSongData(String sentiment, FirestoreCallback firestoreCallback) {
        executor.execute(() -> {
            ArrayList<Songs> songsArrayList = new ArrayList<>();
            db.collection(sentiment.toLowerCase())
                    .get()
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            Songs song = document.toObject(Songs.class);
                            songsArrayList.add(song);
                        }
                        firestoreCallback.onCallback(songsArrayList);
                    })
                    .addOnFailureListener(e -> {
                        firestoreCallback.onFailure(e);
                        Log.e(TAG, "Error getting documents: ", e);
                    });
        });
    }
}
