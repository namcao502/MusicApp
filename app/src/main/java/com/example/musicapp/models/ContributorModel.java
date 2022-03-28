package com.example.musicapp.models;

public class ContributorModel {
    String song_title;
    String user_id;
    String user_email;

    public ContributorModel(String song_title, String user_id, String user_email) {
        this.song_title = song_title;
        this.user_id = user_id;
        this.user_email = user_email;
    }

    public ContributorModel() {
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
