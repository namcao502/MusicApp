package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class PlaylistModel implements Serializable {

    String title;
    List<String> song;
    String id;

    public PlaylistModel(String title, List<String> song) {
        this.title = title;
        this.song = song;
    }

    public PlaylistModel(String title, List<String> song, String id) {
        this.title = title;
        this.song = song;
        this.id = id;
    }

    public PlaylistModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getSong() {
        return song;
    }

    public void setSong(List<String> song) {
        this.song = song;
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
                ", song=" + song +
                ", id='" + id + '\'' +
                '}';
    }
}
