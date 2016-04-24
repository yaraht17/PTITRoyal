package com.ptit.ptitroyal.models;

/**
 * Created by Ken on 21/04/2016.
 */
public class Like {
    private String id;
    private String avatarURI;
    private String name;

    public Like() {
    }

    public Like(String id, String avatarURI, String name) {
        this.id = id;
        this.avatarURI = avatarURI;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatarURI() {
        return avatarURI;
    }

    public void setAvatarURI(String avatarURI) {
        this.avatarURI = avatarURI;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
