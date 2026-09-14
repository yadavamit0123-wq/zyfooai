package com.pt.zyfooai.utils;

import com.pt.zyfooai.model.PostItem;

/**
 * Tracks the active post download so users can scroll away and return later.
 */
public final class PostDownloadTracker {

    public static final class Job {
        public String postKey;
        public int progress;
        public String outputPath;
        public boolean active;
        public boolean complete;
        public String actionType;
    }

    private static Job currentJob;

    private PostDownloadTracker() {
    }

    public static synchronized String postKey(PostItem item) {
        if (item == null) {
            return "";
        }
        if (item.postId != null && !item.postId.isEmpty()) {
            return item.postId;
        }
        return item.image_url != null ? item.image_url : "";
    }

    public static synchronized void start(String postKey, String actionType) {
        currentJob = new Job();
        currentJob.postKey = postKey;
        currentJob.actionType = actionType;
        currentJob.active = true;
        currentJob.complete = false;
        currentJob.progress = 0;
        currentJob.outputPath = null;
    }

    public static synchronized void updateProgress(int progress) {
        if (currentJob != null && currentJob.active) {
            currentJob.progress = Math.max(0, Math.min(100, progress));
        }
    }

    public static synchronized void complete(String outputPath) {
        if (currentJob != null) {
            currentJob.active = false;
            currentJob.complete = true;
            currentJob.progress = 100;
            currentJob.outputPath = outputPath;
        }
    }

    public static synchronized void fail() {
        currentJob = null;
    }

    public static synchronized Job getCurrent() {
        return currentJob;
    }

    public static synchronized boolean isDownloading(String postKey) {
        return currentJob != null
                && currentJob.active
                && postKey != null
                && postKey.equals(currentJob.postKey);
    }

    public static synchronized boolean isComplete(String postKey) {
        return currentJob != null
                && currentJob.complete
                && postKey != null
                && postKey.equals(currentJob.postKey)
                && currentJob.outputPath != null;
    }
}
