package com.example.moodswings.Models;

public class Songs {

    String title,coverImage,singer,songUrl;

    public Songs(){

    }
    public Songs(String title, String coverImage, String singer, String songUrl) {
        this.title = title;
        this.coverImage = coverImage;
        this.singer = singer;
        this.songUrl = songUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getSongUrl() {
        return songUrl;
    }

    public void setSongUrl(String songUrl) {
        this.songUrl = songUrl;
    }
}
