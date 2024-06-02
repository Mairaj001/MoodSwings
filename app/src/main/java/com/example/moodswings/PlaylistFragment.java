package com.example.moodswings;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moodswings.Adapters.SavedSongAdapter;
import com.example.moodswings.Adapters.SongAdapter;
import com.example.moodswings.Firebase.FirebaseRealtimeDb;
import com.example.moodswings.Models.Songs;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlaylistFragment extends Fragment{

    RecyclerView rcv;
    private FirebaseRealtimeDb firebaseRealtimeDB;
    private ExecutorService executorService;
    private SavedSongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        rcv = view.findViewById(R.id.rcvPlaylist);
        rcv.setLayoutManager(new LinearLayoutManager(requireActivity()));

        firebaseRealtimeDB = new FirebaseRealtimeDb();
        executorService = Executors.newSingleThreadExecutor();

        fetchSavedSongs();

        return view;
    }

    private void fetchSavedSongs() {
        firebaseRealtimeDB.getSavedSongs(executorService, new FirebaseRealtimeDb.OnGetSavedSongsListener() {
            @Override
            public void onGetSavedSongs(ArrayList<Songs> savedSongs) {
                if (savedSongs.isEmpty()) {
                    Toast.makeText(requireContext(), "No songs found", Toast.LENGTH_SHORT).show();
                } else {
                    for (Songs song : savedSongs) {
                        Log.d("SavedSong", song.getTitle() + " - " + song.getSongUrl());
                    }
                    requireActivity().runOnUiThread(() -> {
                        adapter = new SavedSongAdapter(requireContext(), savedSongs);
                        rcv.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onCancelled(String errorMessage) {
                // Handle the cancellation
                Log.e("SavedSong", "Failed to retrieve saved songs: " + errorMessage);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
    }
}
