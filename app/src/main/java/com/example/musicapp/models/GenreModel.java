package com.example.musicapp.models;

public class GenreModel {
    String name;
    String img_url;

    public GenreModel(String name, String img_url) {
        this.name = name;
        this.img_url = img_url;
    }

    public GenreModel() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }
}
