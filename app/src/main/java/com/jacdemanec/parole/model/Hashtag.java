package com.jacdemanec.parole.model;

import com.google.firebase.database.ServerValue;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Hashtag {

    private String title;
    private String text;
    private String owner;
    private int likes_count;
    private float lat;
    private float lon;
    private HashMap<String, Boolean> likes;
    private Object timestamp;
    private HashMap<String, Boolean> favorites;
    private String imageUrl;


    public Hashtag() {
    }

    public Hashtag(String title, String text, String owner, String imageUrl, HashMap<String, Boolean> likes, int likes_count, HashMap<String, Boolean> favorites,  float lat, float lon) {
        this.title = title;
        this.text = text;
        this.owner = owner;
        this.likes = likes;
        this.likes_count = likes_count;
        this.lat = lat;
        this.lon = lon;
        this.timestamp = ServerValue.TIMESTAMP;
        this.favorites = favorites;
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLon() {
        return lon;
    }

    public void setLon(float lon) {
        this.lon = lon;
    }

    public int getLikes_count() {
        return likes_count;
    }

    public void setLikes_count(int likes_count) {
        this.likes_count = likes_count;
    }

    public HashMap<String, Boolean> getLikes() {
        return likes;
    }

    public void setLikes(HashMap<String, Boolean> likes) {
        this.likes = likes;
    }

    public Object getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Object timestamp) {
        this.timestamp = timestamp;
    }

    public HashMap<String, Boolean> getFavorites() {
        return favorites;
    }

    public void setFavorites(HashMap<String, Boolean> favorites) {
        this.favorites = favorites;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
