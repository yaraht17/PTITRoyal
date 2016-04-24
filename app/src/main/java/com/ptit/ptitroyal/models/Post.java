package com.ptit.ptitroyal.models;

import java.io.Serializable;

/**
 * Created by Ken on 16/04/2016.
 */
public class Post implements Serializable {
    private String id;
    private Author author;
    private String time;
    private String content;
    private String imageURI;
    private int numberOfLikes;
    private int numberOfComments;
    private boolean isLiked;

    public Post() {
    }

    public Post(String id, Author author, String time, String content, String imageURI, int numberOfLikes, int numberOfComments, boolean isLiked) {
        this.id = id;
        this.author = author;
        this.time = time;
        this.content = content;
        this.imageURI = imageURI;
        this.numberOfLikes = numberOfLikes;
        this.numberOfComments = numberOfComments;
        this.isLiked = isLiked;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public int getNumberOfLikes() {
        return numberOfLikes;
    }

    public void setNumberOfLikes(int numberOfLikes) {
        this.numberOfLikes = numberOfLikes;
    }

    public int getNumberOfComments() {
        return numberOfComments;
    }

    public void setNumberOfComments(int numberOfComments) {
        this.numberOfComments = numberOfComments;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void incLike() {
        ++this.numberOfLikes;
    }

    public void decLike() {
        --this.numberOfLikes;
    }
}
