package com.example.musicapp.models;

import java.io.Serializable;

public class ArtistModel implements Serializable {
    String name;
    String img_url;

    public ArtistModel(String name, String img_url) {
        this.name = name;
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return "ArtistModel{" +
                "name='" + name + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }

    public ArtistModel() {
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
