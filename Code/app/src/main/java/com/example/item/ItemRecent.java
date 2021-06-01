package com.example.item;

public class ItemRecent {
    private String recentId;
    private String recentType;
    private String recentImage;
    private long recentDuration;


    public String getRecentId() {
        return recentId;
    }
    public ItemRecent() {
    }

    public ItemRecent(String movieId, long videoDuration1) {
        this.recentId=movieId;
        this.recentDuration = videoDuration1;
    }

    public long getMovieDuration2() {
        return recentDuration;
    }

    public void setMovieDuration2(long recentDuration) {
        this.recentDuration = recentDuration;
    }

    public void setRecentId(String recentId) {
        this.recentId = recentId;
    }

    public String getRecentType() {
        return recentType;
    }

    public void setRecentType(String recentType) {
        this.recentType = recentType;
    }

    public String getRecentImage() {
        return recentImage;
    }

    public void setRecentImage(String recentImage) {
        this.recentImage = recentImage;
    }
}
