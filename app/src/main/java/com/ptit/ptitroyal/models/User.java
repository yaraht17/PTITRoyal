package com.ptit.ptitroyal.models;

/**
 * Created by HoangTien on 4/20/16.
 */
public class User {

    private String id;
    private String name;
    private String avatar;
    private String cover;
    private String gender;
    private String email;

    public User(String id, String name, String avatar, String cover, String gender, String email) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
        this.cover = cover;
        this.gender = gender;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
