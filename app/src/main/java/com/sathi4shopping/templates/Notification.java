package com.sathi4shopping.templates;

public class Notification {
    private String notification;
    private String  date;
    private String time;

    public Notification(String notification, String date,String time) {
        this.notification = notification;
        this.date = date;
        this.time=time;
    }

    public String getTime() {
        return time;
    }

    public Notification() {
    }

    public String getNotification() { return notification; }

    public String getDate() {
        return date;
    }
}
