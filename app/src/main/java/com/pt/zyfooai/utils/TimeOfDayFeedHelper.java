package com.pt.zyfooai.utils;

import androidx.annotation.Nullable;

import com.pt.zyfooai.model.CategoryItem;
import com.pt.zyfooai.model.PostItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Orders home feed by local time-of-day greeting + today's-date categories,
 * then other / trending posts.
 *
 * <pre>
 * Morning   (5–11)  → Good Morning
 * Afternoon (12–16) → Good Afternoon
 * Evening   (17–20) → Good Evening / Good Night
 * Night     (21–4)  → Good Night
 * + Days Special / आज के दर्शन
 * + remaining feed
 * </pre>
 */
public final class TimeOfDayFeedHelper {

    public enum Slot {
        MORNING,
        AFTERNOON,
        EVENING,
        NIGHT
    }

    /** Fallback IDs from live getCategory (if name match not ready yet). */
    private static final String FALLBACK_MORNING = "23";
    private static final String FALLBACK_AFTERNOON = "40";
    private static final String FALLBACK_NIGHT = "6";
    private static final String FALLBACK_DAYS_SPECIAL = "22";
    private static final String FALLBACK_AAJ_KE_DARSHAN = "10";

    private TimeOfDayFeedHelper() {
    }

    public static Slot currentSlot() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12) {
            return Slot.MORNING;
        }
        if (hour >= 12 && hour < 17) {
            return Slot.AFTERNOON;
        }
        if (hour >= 17 && hour < 21) {
            return Slot.EVENING;
        }
        return Slot.NIGHT;
    }

    @Nullable
    public static String findGreetingCategoryId(@Nullable List<CategoryItem> categories) {
        Slot slot = currentSlot();
        if (categories != null) {
            for (CategoryItem item : categories) {
                if (item == null || item.name == null || item.id == null) {
                    continue;
                }
                if (matchesGreeting(item.name, slot)) {
                    return item.id;
                }
            }
        }
        switch (slot) {
            case MORNING:
                return FALLBACK_MORNING;
            case AFTERNOON:
                return FALLBACK_AFTERNOON;
            case EVENING:
            case NIGHT:
            default:
                return FALLBACK_NIGHT;
        }
    }

    /** Days Special + आज के दर्शन (and similar name matches). */
    public static List<String> findDateCategoryIds(@Nullable List<CategoryItem> categories) {
        List<String> ids = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        if (categories != null) {
            for (CategoryItem item : categories) {
                if (item == null || item.name == null || item.id == null) {
                    continue;
                }
                if (matchesDateCategory(item.name) && seen.add(item.id)) {
                    ids.add(item.id);
                }
            }
        }
        if (ids.isEmpty()) {
            ids.add(FALLBACK_DAYS_SPECIAL);
            ids.add(FALLBACK_AAJ_KE_DARSHAN);
        }
        return ids;
    }

    /**
     * Merge: greeting → date → remaining (trending/other), with images leading
     * inside the greeting block so open still feels fast.
     */
    public static List<PostItem> merge(
            @Nullable List<PostItem> greetingPosts,
            @Nullable List<PostItem> datePosts,
            @Nullable List<PostItem> allPosts,
            int leadingImagesInGreeting
    ) {
        List<PostItem> ordered = new ArrayList<>();
        Set<String> placed = new HashSet<>();

        appendUnique(ordered, placed,
                FeedOrderHelper.imagesFirst(
                        greetingPosts != null ? greetingPosts : Collections.emptyList(),
                        leadingImagesInGreeting));
        appendUnique(ordered, placed, datePosts);
        appendUnique(ordered, placed, allPosts);
        return ordered;
    }

    private static void appendUnique(
            List<PostItem> out,
            Set<String> placed,
            @Nullable List<PostItem> source
    ) {
        if (source == null) {
            return;
        }
        for (PostItem post : source) {
            if (post == null) {
                continue;
            }
            String key = postKey(post);
            if (key != null && !placed.add(key)) {
                continue;
            }
            if (key == null) {
                out.add(post);
            } else {
                out.add(post);
            }
        }
    }

    @Nullable
    private static String postKey(PostItem post) {
        if (post.postId != null && !post.postId.trim().isEmpty()) {
            return post.postId.trim();
        }
        if (post.image_url != null && !post.image_url.trim().isEmpty()) {
            return post.image_url.trim();
        }
        return null;
    }

    private static boolean matchesGreeting(String rawName, Slot slot) {
        String name = normalize(rawName);
        switch (slot) {
            case MORNING:
                return name.contains("good morning")
                        || name.contains("suprabhat")
                        || name.contains("शुभ प्रभात")
                        || name.contains("goodmorning");
            case AFTERNOON:
                return name.contains("good afternoon")
                        || name.contains("शुभ दोपहर")
                        || name.contains("goodafternoon");
            case EVENING:
                return name.contains("good evening")
                        || name.contains("शुभ संध्या")
                        || name.contains("goodevening")
                        || name.contains("good night")
                        || name.contains("शुभ रात्रि")
                        || name.contains("goodnight");
            case NIGHT:
            default:
                return name.contains("good night")
                        || name.contains("शुभ रात्रि")
                        || name.contains("goodnight")
                        || name.contains("good evening");
        }
    }

    private static boolean matchesDateCategory(String rawName) {
        String name = normalize(rawName);
        return name.contains("days special")
                || name.contains("day special")
                || name.contains("today")
                || name.contains("आज के दर्शन")
                || name.contains("aaj ke")
                || name.contains("date")
                || name.contains("special day");
    }

    private static String normalize(String value) {
        if (value == null) {
            return "";
        }
        // Strip emojis / symbols for matching; keep letters/digits/spaces + Devanagari.
        String cleaned = value.replaceAll("[^\\p{L}\\p{N}\\s]+", " ");
        return cleaned.toLowerCase(Locale.ROOT).replaceAll("\\s+", " ").trim();
    }
}
