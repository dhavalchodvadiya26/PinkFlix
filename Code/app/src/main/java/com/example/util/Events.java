package com.example.util;

public class Events {
    public static class FullScreen {
        private boolean isFullScreen = false;

        public boolean isFullScreen() {
            return isFullScreen;
        }

        public void setFullScreen(boolean fullScreen) {
            isFullScreen = fullScreen;
        }
    }
}
