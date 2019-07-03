package com.sathi4shopping.templates;

public class Trending {
    private String title;
    private String link;
    private String time;
    private String description;

    public Trending() {
    }

    public Trending(String title, String time, String description,String link) {
        this.title = title;
        this.time = time;
        this.description = description;
        this.link=link;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() { return link; }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
