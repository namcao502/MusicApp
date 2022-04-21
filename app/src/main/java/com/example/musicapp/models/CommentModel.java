package com.example.musicapp.models;

public class CommentModel {

    String user_id;
    String song_id;
    String detail;
    String user_email;


    public CommentModel() {
    }

    public CommentModel(String user_id, String song_id, String detail, String user_email) {
        this.user_id = user_id;
        this.song_id = song_id;
        this.detail = detail;
        this.user_email = user_email;
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

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }
}
