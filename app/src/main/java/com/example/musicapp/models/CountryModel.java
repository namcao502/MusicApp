package com.example.musicapp.models;

import java.io.Serializable;

public class CountryModel implements Serializable {
    String id;
    String name;
    String img_url;

    public CountryModel(String name, String img_url) {
        this.name = name;
        this.img_url = img_url;
    }

    public CountryModel(String id, String name, String img_url) {
        this.id = id;
        this.name = name;
        this.img_url = img_url;
    }

    @Override
    public String toString() {
        return id + ", " + name;
    }

    public CountryModel() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
