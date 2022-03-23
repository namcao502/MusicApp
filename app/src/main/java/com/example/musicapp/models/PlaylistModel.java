package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class PlaylistModel implements Serializable {

    String title;
    List<String> song;
    String ID;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public PlaylistModel() {

    }

    public PlaylistModel(String title, List<String> song) {
        this.title = title;
        this.song = song;
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

    @Override
    public String toString() {
        return "PlaylistModel{" +
                "title='" + title + '\'' +
                ", song=" + song +
                ", ID='" + ID + '\'' +
                '}';
    }
}
