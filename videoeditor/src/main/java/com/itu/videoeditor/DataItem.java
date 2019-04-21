package com.itu.videoeditor;

/**
 * Created by Jia Liu on 4/20/2019.
 */
public class DataItem {
    boolean isVideo = false;
    float startTime = 0; // in sec
    float length = 0;
    int x = 0;
    int y = 0;
    int width = 0;
    int height = 0;
    float speed = 1.0f;
    int rotation = 0;
    boolean isFlipped = false;

    public DataItem(boolean isVideo, float length, int width, int height) {
        this.isVideo = isVideo;
        this.length = length;
        this.width = width;
        this.height = height;
    }
}
