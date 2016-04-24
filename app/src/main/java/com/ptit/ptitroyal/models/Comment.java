package com.ptit.ptitroyal.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Ken on 17/04/2016.
 */
public class Comment implements Serializable {
    private String id;
    private Author author;
    private Author replyTo;
    private String time;
    private String content;
    private List<User> mentioned;

    public Comment() {
    }

    public Comment(String id, Author author, Author replyTo, String content, String time, List<User> mentioned) {
        this.id = id;
        this.author = author;
        this.replyTo = replyTo;
        this.time = time;
        this.content = content;
        this.mentioned = mentioned;
    }

    public Comment(String id, Author author, Author replyTo, String content, String time) {

        this.id = id;
        this.author = author;
        this.replyTo = replyTo;
        this.time = time;
        this.content = content;
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

    public Author getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Author replyTo) {
        this.replyTo = replyTo;
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

    public List<User> getMentioned() {
        return mentioned;
    }

    public void setMentioned(List<User> mentioned) {
        this.mentioned = mentioned;
    }

    @Override
    public String toString() {
        return author.getId() + "-" + author.getUsername() + "-" + author.getAvatarURI() + "-" + content + "-" + time;
    }
}
