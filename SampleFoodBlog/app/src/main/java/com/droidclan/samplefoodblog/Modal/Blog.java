package com.droidclan.samplefoodblog.Modal;


public class Blog {

    private String User;
    private int Views;
    private String Image;
    private long Time;
    private String Title;
    private String Desc;
    private String Details;


    public Blog(){}

    public Blog(String user, int views, String image, long time, String title, String desc, String details) {
        User = user;
        Views = views;
        Image = image;
        Time = time;
        Title = title;
        Desc = desc;
        Details = details;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public int getViews() {
        return Views;
    }

    public void setViews(int views) {
        Views = views;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getDetails() {
        return Details;
    }

    public void setDetails(String details) {
        Details = details;
    }

}
