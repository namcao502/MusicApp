package com.example.musicapp.models;

import java.io.Serializable;
import java.util.List;

public class SongModel implements Serializable {

    private String title;
    private String url;
    private String img_url;
    private List<ArtistModel> Artist;

    public SongModel() {
    }

    public SongModel(String title, String url, String img_url, List<ArtistModel> Artist) {
        this.title = title;
        this.url = url;
        this.img_url = img_url;
        this.Artist = Artist;
    }

    public SongModel(String title, String url, String img_url) {
        this.title = title;
        this.url = url;
        this.img_url = img_url;
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

    public List<ArtistModel> getArtist() {
        return Artist;
    }

    public void setArtist(List<ArtistModel> artist) {
        Artist = artist;
    }

    @Override
    public String toString() {
        return "SongModel{" +
                "title='" + title + '\'' +
                ", url='" + url + '\'' +
                ", img_url='" + img_url + '\'' +
                ", artistModelList=" + Artist +
                '}';
    }

}
