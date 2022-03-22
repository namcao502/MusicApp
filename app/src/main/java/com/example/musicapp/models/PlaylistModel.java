package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class PlaylistModel implements Serializable {

    String name;
    List<String> song;

    public PlaylistModel() {

    }

    public PlaylistModel(String name, List<String> song) {
        this.name = name;
        this.song = song;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
                "name='" + name + '\'' +
                ", song=" + song +
                '}';
    }
}
