package com.pt.zyfooai.model;

import android.net.Uri;

public class DownloadItem {

    public Uri uri;
    public boolean isVideo;
    public String dateLabel;
    public String sizeLabel;

    public DownloadItem(Uri uri, boolean isVideo) {
        this.uri = uri;
        this.isVideo = isVideo;
    }

    public DownloadItem(Uri uri, boolean isVideo, String dateLabel, String sizeLabel) {
        this.uri = uri;
        this.isVideo = isVideo;
        this.dateLabel = dateLabel;
        this.sizeLabel = sizeLabel;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setVideo(boolean video) {
        isVideo = video;
    }

    public String getDateLabel() {
        return dateLabel;
    }

    public String getSizeLabel() {
        return sizeLabel;
    }
}
