package com.ptit.ptitroyal.models;

/**
 * Created by HoangTien on 4/25/16.
 */
public class Topic {

    private String name;
    private String icon;

    public Topic(String name, String icon) {
        this.name = name;
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
