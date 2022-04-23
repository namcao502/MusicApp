package com.example.musicapp.models;

public class UserModel {
    String id;
    String img_url;
    String name;

    public UserModel() {
    }

    public UserModel(String id, String img_url, String name) {
        this.id = id;
        this.img_url = img_url;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "id='" + id + '\'' +
                ", img_url='" + img_url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
