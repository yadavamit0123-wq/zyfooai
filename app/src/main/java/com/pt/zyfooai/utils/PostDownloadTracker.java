package com.pt.zyfooai.utils;

import com.pt.zyfooai.model.PostItem;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Tracks multiple concurrent post downloads so the user can scroll and download
 * other reels while one (or more) continue in the background.
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

    private static final int MAX_COMPLETED = 40;
    private static final Map<String, Job> jobs = new LinkedHashMap<>();

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
        if (postKey == null || postKey.isEmpty()) {
            return;
        }
        Job job = new Job();
        job.postKey = postKey;
        job.actionType = actionType;
        job.active = true;
        job.complete = false;
        job.progress = 0;
        job.outputPath = null;
        jobs.put(postKey, job);
    }

    public static synchronized void updateProgress(String postKey, int progress) {
        Job job = jobs.get(postKey);
        if (job != null && job.active) {
            job.progress = Math.max(0, Math.min(100, progress));
        }
    }

    public static synchronized void complete(String postKey, String outputPath) {
        Job job = jobs.get(postKey);
        if (job == null) {
            return;
        }
        job.active = false;
        job.complete = true;
        job.progress = 100;
        job.outputPath = outputPath;
        trimCompleted();
    }

    public static synchronized void fail(String postKey) {
        if (postKey != null) {
            jobs.remove(postKey);
        }
    }

    public static synchronized Job getJob(String postKey) {
        if (postKey == null) {
            return null;
        }
        return jobs.get(postKey);
    }

    public static synchronized boolean isDownloading(String postKey) {
        Job job = jobs.get(postKey);
        return job != null && job.active;
    }

    public static synchronized boolean isComplete(String postKey) {
        Job job = jobs.get(postKey);
        return job != null
                && job.complete
                && job.outputPath != null
                && !job.outputPath.isEmpty();
    }

    public static synchronized boolean hasActiveDownloads() {
        for (Job job : jobs.values()) {
            if (job != null && job.active) {
                return true;
            }
        }
        return false;
    }

    private static void trimCompleted() {
        int completed = 0;
        for (Job job : jobs.values()) {
            if (job != null && job.complete) {
                completed++;
            }
        }
        if (completed <= MAX_COMPLETED) {
            return;
        }
        Iterator<Map.Entry<String, Job>> it = jobs.entrySet().iterator();
        while (it.hasNext() && completed > MAX_COMPLETED) {
            Map.Entry<String, Job> entry = it.next();
            Job job = entry.getValue();
            if (job != null && job.complete && !job.active) {
                it.remove();
                completed--;
            }
        }
    }
}
