package com.example.moodswings;

import android.os.Bundle;
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

public class PlaylistFragment extends Fragment {
    RecyclerView rcv;
    FirebaseRealtimeDb firebaseRealtimeDb;
    ExecutorService executor;
    SavedSongAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.playlist_fragment, container, false);
        rcv = view.findViewById(R.id.rcvPlaylist);

        rcv.setLayoutManager(new LinearLayoutManager(requireContext()));

        executor = Executors.newSingleThreadExecutor();
        firebaseRealtimeDb = new FirebaseRealtimeDb();
        showSavedSongs();

        return view;
    }

    private void showSavedSongs() {
        firebaseRealtimeDb.getSavedSongs(executor, new FirebaseRealtimeDb.OnGetSavedSongsListener() {
            @Override
            public void onGetSavedSongs(ArrayList<Songs> savedSongs) {
                if (savedSongs.isEmpty()) {
                    requireActivity().runOnUiThread(() ->
                            Toast.makeText(requireContext(), "You haven't added songs", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    requireActivity().runOnUiThread(() -> {
                        adapter = new SavedSongAdapter(requireContext(), savedSongs);
                        rcv.setAdapter(adapter);
                    });
                }
            }

            @Override
            public void onCancelled(String errorMessage) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
