
package com.example.model.menu;

import com.google.gson.annotations.SerializedName;

public class MenuCategory {

    @SerializedName("status_code")
    private Long mStatusCode;
    @SerializedName("VIDEO_STREAMING_APP")
    private VideoStreamApp mVideoStreamApp;

    public Long getStatusCode() {
        return mStatusCode;
    }

    public VideoStreamApp getVideoStreamApp() {
        return mVideoStreamApp;
    }

    public static class Builder {

        private Long mStatusCode;
        private VideoStreamApp mVideoStreamApp;

        public MenuCategory.Builder withStatusCode(Long statusCode) {
            mStatusCode = statusCode;
            return this;
        }

        public MenuCategory.Builder withVideoStreamApp(VideoStreamApp VideoStreamApp) {
            mVideoStreamApp = VideoStreamApp;
            return this;
        }

        public MenuCategory build() {
            MenuCategory menuCategory = new MenuCategory();
            menuCategory.mStatusCode = mStatusCode;
            menuCategory.mVideoStreamApp = mVideoStreamApp;
            return menuCategory;
        }

    }

}
