package com.example.hunter;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Posts {
    private String caption;
    private Date date_created;
    private String photo_id;
    private String user_id;
    private String place_id;
    private List<Like> likes;
    private List<Comment> comments;
    private float rating;

    public Posts(){

    }

    public Posts(String caption, Date date_created, String photo_id, String user_id, String place_id, List<Like> likes, List<Comment> comments, float rating) {
        this.caption = caption;
        this.date_created = date_created;
        this.photo_id = photo_id;
        this.user_id = user_id;
        this.place_id = place_id;
        this.likes = likes;
        this.comments = comments;
        this.rating=rating;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public Date getDate_created() {
        return date_created;
    }

    public void setDate_created(Date date_created) {
        this.date_created = date_created;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getPlace_id() {
        return place_id;
    }

    public void setPlace_id(String place_id) {
        this.place_id = place_id;
    }

    public List<Like> getLikes() {
        return likes;
    }

    public void setLikes(List<Like> likes) {
        this.likes = likes;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
