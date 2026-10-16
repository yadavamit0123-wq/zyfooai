package com.pt.zyfooai.utils;

import com.pt.zyfooai.model.PostItem;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Feed ordering helpers for cold open speed + time-of-day priority.
 */
public final class FeedOrderHelper {

    /** How many image posts to lead with inside a greeting block. */
    public static final int LEADING_IMAGE_COUNT = 3;

    private FeedOrderHelper() {
    }

    /**
     * Puts up to {@code leadingImages} image posts first (when available),
     * then the rest in original order.
     */
    public static List<PostItem> imagesFirst(List<PostItem> posts, int leadingImages) {
        if (posts == null || posts.isEmpty() || leadingImages <= 0) {
            return posts == null ? new ArrayList<>() : new ArrayList<>(posts);
        }

        List<PostItem> images = new ArrayList<>();
        boolean hasVideo = false;
        for (PostItem post : posts) {
            if (post == null) {
                continue;
            }
            if (post.is_video) {
                hasVideo = true;
            } else {
                images.add(post);
            }
        }
        if (!hasVideo || images.isEmpty()) {
            return new ArrayList<>(posts);
        }

        int lead = Math.min(leadingImages, images.size());
        List<PostItem> ordered = new ArrayList<>(posts.size());
        Set<String> placed = new HashSet<>();
        for (int i = 0; i < lead; i++) {
            PostItem image = images.get(i);
            ordered.add(image);
            String key = keyOf(image);
            if (key != null) {
                placed.add(key);
            }
        }
        for (PostItem post : posts) {
            if (post == null) {
                continue;
            }
            String key = keyOf(post);
            if (key != null && placed.contains(key)) {
                continue;
            }
            ordered.add(post);
            if (key != null) {
                placed.add(key);
            }
        }
        return ordered;
    }

    private static String keyOf(PostItem post) {
        if (post.postId != null && !post.postId.trim().isEmpty()) {
            return post.postId.trim();
        }
        if (post.image_url != null && !post.image_url.trim().isEmpty()) {
            return post.image_url.trim();
        }
        return null;
    }
}
