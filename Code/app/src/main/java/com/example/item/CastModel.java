package com.example.item;

public class CastModel {
    private String castImage;
    private String castName;
    private String id;

    public CastModel(String castImage, String castName, String id) {
        this.castImage = castImage;
        this.castName = castName;
        this.id = id;
    }

    public String getCastImage() {
        
        return castImage;
    }

    public void setCastImage(String castImage) {
        this.castImage = castImage;
    }

    public String getCastName() {
        return castName;
    }

    public void setCastName(String castName) {
        this.castName = castName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
