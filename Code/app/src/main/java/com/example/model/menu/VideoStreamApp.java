package com.example.model.menu;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class VideoStreamApp {

    @SerializedName("category")
    private List<Category> mCategory;

    public List<Category> getCategory() {
        return mCategory;
    }

    public static class Builder {

        private List<Category> mCategory;

        public VideoStreamApp.Builder withCategory(List<Category> category) {
            mCategory = category;
            return this;
        }

        public VideoStreamApp build() {
            VideoStreamApp vIDEOSTREAMINGAPP = new VideoStreamApp();
            vIDEOSTREAMINGAPP.mCategory = mCategory;
            return vIDEOSTREAMINGAPP;
        }

    }

}
