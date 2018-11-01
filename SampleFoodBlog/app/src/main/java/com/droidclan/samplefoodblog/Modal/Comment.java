package com.droidclan.samplefoodblog.Modal;


import android.support.design.widget.NavigationView;

public class Comment {

    private String Comment;
    private String User;
    private long Time;
    private String Name;

    public Comment(){}

    public Comment(String comment, String user, long time, String name) {
        Comment = comment;
        User = user;
        Time = time;
        Name = name;
    }

    public void setName(String name){
        Name = name;
    }

    public String getName(){
        return Name;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long time) {
        Time = time;
    }
}
