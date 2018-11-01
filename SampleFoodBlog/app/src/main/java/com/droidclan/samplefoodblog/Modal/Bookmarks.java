package com.droidclan.samplefoodblog.Modal;


public class Bookmarks {

    private String Title;
    private long Time;
    private String Desc;
    private String Image;
    private long BookmarkTime;
    private String User;


    public Bookmarks() {}

    public Bookmarks(String title, long time, String desc, String image, long bookmarkTime, String user) {
        Title = title;
        Time = time;
        Desc = desc;
        Image = image;
        BookmarkTime = bookmarkTime;
        User = user;

    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public long getBookmarkTime() {
        return BookmarkTime;
    }

    public void setBookmarkTime(long bookmarkTime) {
        BookmarkTime = bookmarkTime;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }
}
