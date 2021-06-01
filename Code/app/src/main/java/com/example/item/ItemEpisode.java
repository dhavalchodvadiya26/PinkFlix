package com.example.item;

import java.util.ArrayList;

public class ItemEpisode {
    private String episodeId;
    private String episodeName;
    private String episodeUrl;
    private String episodeType;
    private String episodeImage;
    private String premium;
    private boolean isPremium = false;
    private boolean isDownload = false;
    private String downloadUrl;
    private String episodeDate;
    private String episodeDuration;
    private String rental_plan;
    private boolean check_plan;
    private String description;

    private ArrayList<Resolution> lstResolution;

    public String getEpisodeId() {
        return episodeId;
    }

    public void setEpisodeId(String episodeId) {
        this.episodeId = episodeId;
    }

    public String getEpisodeName() {
        return episodeName;
    }

    public void setEpisodeName(String episodeName) {
        this.episodeName = episodeName;
    }

    public String getEpisodeUrl() {
        return episodeUrl;
    }

    public void setEpisodeUrl(String episodeUrl) {
        this.episodeUrl = episodeUrl;
    }

    public String getEpisodeType() {
        return episodeType;
    }

    public void setEpisodeType(String episodeType) {
        this.episodeType = episodeType;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public String getPremium() {
        return premium;
    }

    public void setPremium(String premium) {
        this.premium = premium;
    }

    public String getEpisodeImage() {
        return episodeImage;
    }

    public void setEpisodeImage(String episodeImage) {
        this.episodeImage = episodeImage;
    }

    public boolean isDownload() {
        return isDownload;
    }

    public void setDownload(boolean download) {
        isDownload = download;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getEpisodeDate() {
        return episodeDate;
    }

    public void setEpisodeDate(String episodeDate) {
        this.episodeDate = episodeDate;
    }

    public String getEpisodeDuration() {
        return episodeDuration;
    }

    public void setEpisodeDuration(String episodeDuration) {
        this.episodeDuration = episodeDuration;
    }

    public static class Resolution {
        String series_id;
        String movie_resolution;
        String movie_url;

        public String getMovie_id() {
            return series_id;
        }

        public void setMovie_id(String movie_id) {
            this.series_id = movie_id;
        }

        public String getMovie_resolution() {
            return movie_resolution;
        }

        public void setMovie_resolution(String movie_resolution) {
            this.movie_resolution = movie_resolution;
        }

        public String getMovie_url() {
            return movie_url;
        }

        public void setMovie_url(String movie_url) {
            this.movie_url = movie_url;
        }
    }

    public ArrayList<Resolution> getLstResolution() {
        return lstResolution;
    }

    public void setLstResolution(ArrayList<Resolution> lstResolution) {
        this.lstResolution = lstResolution;
    }

    public boolean isCheck_plan() {
        return check_plan;
    }

    public void setCheck_plan(boolean check_plan) {
        this.check_plan = check_plan;
    }

    public String getRental_plan() {
        return rental_plan;
    }

    public void setRental_plan(String rental_plan) {
        this.rental_plan = rental_plan;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
