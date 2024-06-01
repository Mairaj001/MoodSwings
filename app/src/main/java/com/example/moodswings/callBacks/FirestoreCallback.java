package com.example.moodswings.callBacks;

import com.example.moodswings.Models.Songs;

import java.util.List;

public interface FirestoreCallback {
    void onCallback(List<Songs> songList);
}