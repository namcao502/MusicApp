package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class PlaylistModel implements Serializable {

    String title;
    List<String> song_id;
    String id;

    public PlaylistModel(String title, List<String> song_id) {
        this.title = title;
        this.song_id = song_id;
    }

    public PlaylistModel(String title, List<String> song_id, String id) {
        this.title = title;
        this.song_id = song_id;
        this.id = id;
    }

    public PlaylistModel() {
    }

    public List<String> getSong_id() {
        return song_id;
    }

    public void setSong_id(List<String> song_id) {
        this.song_id = song_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "PlaylistModel{" +
                "title='" + title + '\'' +
                ", song_id=" + song_id +
                ", id='" + id + '\'' +
                '}';
    }
}
