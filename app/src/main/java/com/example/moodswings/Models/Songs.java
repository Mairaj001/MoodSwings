package com.example.moodswings.Models;

public class Songs {

    String Title,coverImage,singer,songUrl;

    public Songs(){

    }
    public Songs(String title, String coverImage, String singer, String songUrl) {
        this.Title = title;
        this.coverImage = coverImage;
        this.singer = singer;
        this.songUrl = songUrl;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        this.Title = title;
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
