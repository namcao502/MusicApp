package com.example.musicapp.models;

import com.google.firebase.firestore.FirebaseFirestore;

public class CommentModel {

    String user_id;
    String song_id;
    String detail;

    public CommentModel() {
    }

    public CommentModel(String user_id, String song_id, String detail) {
        this.user_id = user_id;
        this.song_id = song_id;
        this.detail = detail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSong_id() {
        return song_id;
    }

    public void setSong_id(String song_id) {
        this.song_id = song_id;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }


}
