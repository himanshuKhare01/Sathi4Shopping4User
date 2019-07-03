package com.sathi4shopping.templates;
public class UserDisplayTemplate {
    private String title;
    private String subtitle;
    private String uid;
    private String type;
    private String uri;
    private String lasttext;
    private String count;
    private String  visible;

    public UserDisplayTemplate(String s1, String s2,String s3,String s4,String s5,String s6,String s7, String s8){
        title=s1;
        subtitle=s2;
        type=s3;
        uid=s4;
        uri=s5;
        lasttext=s6;
        count=s7;
        visible=s8;
    }

    public String getVisible() { return visible; }
    public String getCount() { return count; }
    public String getTitle() {
        return title;
    }
    public String getSubtitle() {
        return subtitle;
    }
    public String getType() { return type; }
    public String getUid() { return uid; }
    public String getUri() { return uri; }
    public String getLasttext() { return lasttext; }
}