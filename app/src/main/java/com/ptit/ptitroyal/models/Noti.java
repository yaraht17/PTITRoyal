package com.ptit.ptitroyal.models;

/**
 * Created by Manh on 4/21/16.
 */
public class Noti {
    private String id;
    private String fromID;
    private String fromName;
    private String fromAvatar;
    private int type;
    private String post;
    private String createDate;
    private String read;

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public Noti(String id, String fromID, String fromName, String fromAvatar, int type, String post, String createDate, String read) {
        this.id = id;
        this.fromID = fromID;
        this.fromName = fromName;
        this.fromAvatar = fromAvatar;
        this.type = type;
        this.post = post;
        this.createDate = createDate;
        this.read = read;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFromID() {
        return fromID;
    }

    public void setFromID(String fromID) {
        this.fromID = fromID;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getFromAvatar() {
        return fromAvatar;
    }

    public void setFromAvatar(String fromAvatar) {
        this.fromAvatar = fromAvatar;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getPost() {
        return post;
    }

    public void setPost(String post) {
        this.post = post;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
