package com.sathi4shopping.templates;

public class Home {
    private String title;
    private String subtitle;
    private String time;
    private String image;
    private String description;

    public Home() {
    }

    public Home(String title, String subtitle, String time, String image, String description) {
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.image = image;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
