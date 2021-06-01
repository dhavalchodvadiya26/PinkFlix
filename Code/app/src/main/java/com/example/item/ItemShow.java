package com.example.item;

import java.util.ArrayList;

public class ItemShow {

    private String showId;
    private String showName;
    private String showImage;
    private String showDescription;
    private String showLanguage;
    private String showRating;
    private String strTime;
    private ArrayList<Videos> listVideo;
    private ArrayList<ItemShow.Resolution> lstResolution;



    public static class Resolution {
        String show_id;
        String show_resolution;
        String show_url;

        public String getShow_id() {
            return show_id;
        }

        public void setShow_id(String movie_id) {
            this.show_id = show_id;
        }

        public String getShow_resolution() {
            return show_resolution;
        }

        public void setShow_resolution(String show_resolution) {
            this.show_resolution = show_resolution;
        }

        public String getShow_url() {
            return show_url;
        }

        public void setShow_url(String show_url) {
            this.show_url = show_url;
        }
    }


    public static class Videos {
        String language;
        String url;

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public ArrayList<Videos> getListVideo() {
        return listVideo;
    }

    public void setListVideo(ArrayList<Videos> listVideo) {
        this.listVideo = listVideo;
    }


    public ArrayList<ItemShow.Resolution> getLstResolution() {
        return lstResolution;
    }

    public void setLstResolution(ArrayList<ItemShow.Resolution> lstResolution) {
        this.lstResolution = lstResolution;
    }


    public String getStrTime() {
        return strTime;
    }

    public void setStrTime(String strTime) {
        this.strTime = strTime;
    }

    public String getShowId() {
        return showId;
    }

    public void setShowId(String showId) {
        this.showId = showId;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getShowImage() {
        return showImage;
    }

    public void setShowImage(String showImage) {
        this.showImage = showImage;
    }

    public String getShowDescription() {
        return showDescription;
    }

    public void setShowDescription(String showDescription) {
        this.showDescription = showDescription;
    }

    public String getShowLanguage() {
        return showLanguage;
    }

    public void setShowLanguage(String showLanguage) {
        this.showLanguage = showLanguage;
    }

    public String getShowRating() {
        return showRating;
    }

    public void setShowRating(String showRating) {
        this.showRating = showRating;
    }
}
