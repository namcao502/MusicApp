package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class SongModel implements Serializable {

    private String id;
    private String title;
    private String url;
    private String img_url;
    private List<String> artist;
    private List<String> country;
    private List<String> genre;

    public SongModel(String title, String url, String img_url, List<String> artist, List<String> country, List<String> genre) {
        this.title = title;
        this.url = url;
        this.img_url = img_url;
        this.artist = artist;
        this.country = country;
        this.genre = genre;
    }

    public SongModel(String id, String title, String url, String img_url, List<String> artist, List<String> country, List<String> genre) {
        this.id = id;
        this.title = title;
        this.url = url;
        this.img_url = img_url;
        this.artist = artist;
        this.country = country;
        this.genre = genre;
    }

    public List<String> getCountry() {
        return country;
    }

    public void setCountry(List<String> country) {
        this.country = country;
    }

    public List<String> getGenre() {
        return genre;
    }

    public void setGenre(List<String> genre) {
        this.genre = genre;
    }

    public SongModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public List<String> getArtist() {
        return artist;
    }

    public void setArtist(List<String> artist) {
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id + ", " + title;
    }
}
