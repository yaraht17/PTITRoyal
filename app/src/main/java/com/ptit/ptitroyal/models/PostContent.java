package com.ptit.ptitroyal.models;

/**
 * Created by Manh on 4/24/16.
 */
public class PostContent {
    private String content;
    private String image;
    private String topic;

    public PostContent(String content, String image, String topic) {
        this.content = content;
        this.image = image;
        this.topic = topic;
    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
