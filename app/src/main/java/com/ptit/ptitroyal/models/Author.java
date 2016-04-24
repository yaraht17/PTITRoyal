package com.ptit.ptitroyal.models;

import java.io.Serializable;

/**
 * Created by Ken on 18/04/2016.
 */
public class Author implements Serializable {
    private String id;
    private String username;
    private String avatarURI;

    public Author() {
    }

    public Author(String id, String username, String avatarURI) {

        this.id = id;
        this.username = username;
        this.avatarURI = avatarURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

}
