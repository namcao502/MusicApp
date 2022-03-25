package com.example.musicapp.models;

public class CommentModel {

    String user_id;
    String song_title;
    String detail;
    String user_email;


    public CommentModel() {
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getSong_title() {
        return song_title;
    }

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public CommentModel(String user_id, String song_title, String detail) {
        this.user_id = user_id;
        this.song_title = song_title;
        this.detail = detail;
    }

    public CommentModel(String user_id, String song_title, String detail, String user_email) {
        this.user_id = user_id;
        this.song_title = song_title;
        this.detail = detail;
        this.user_email = user_email;
    }
}
