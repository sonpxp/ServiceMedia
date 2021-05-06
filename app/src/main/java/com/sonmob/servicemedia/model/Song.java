package com.sonmob.servicemedia.model;

import java.io.Serializable;

public class Song implements Serializable {
    private String title;
    private String single;
    private int image;
    private int resource;

    public Song(String title, String single, int image, int resource) {
        this.title = title;
        this.single = single;
        this.image = image;
        this.resource = resource;
    }

    public Song(String noi_nay_co_anh, String son_tung, int file_music) {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSingle() {
        return single;
    }

    public void setSingle(String single) {
        this.single = single;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }
}
