package com.pt.zyfooai.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.pt.zyfooai.R;
import com.pt.zyfooai.model.SeasonalStickerConfig;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * P2: limited-time festival sticker prompts (Remote Config + local date fallback).
 */
public final class SeasonalStickerHelper {

    private static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd", Locale.US);

    private SeasonalStickerHelper() {
    }

    public static boolean isSeasonalActive(PreferenceManager preferenceManager) {
        return getActiveServerConfig(preferenceManager) != null
                || resolveLocalSeason() != null;
    }

    public static boolean prefersSeasonalFrame(PreferenceManager preferenceManager) {
        SeasonalStickerConfig server = getActiveServerConfig(preferenceManager);
        if (server != null) {
            return server.isForceDarkGold();
        }
        return resolveLocalSeason() != null;
    }

    public static String getActivePrompt(Context context, PreferenceManager preferenceManager) {
        if (context == null) {
            return null;
        }
        SeasonalStickerConfig server = getActiveServerConfig(preferenceManager);
        if (server != null && server.getPrompt() != null && !server.getPrompt().trim().isEmpty()) {
            return server.getPrompt().trim();
        }
        LocalSeason local = resolveLocalSeason();
        if (local != null) {
            return context.getString(local.promptRes);
        }
        return null;
    }

    public static String getActiveSeasonId(PreferenceManager preferenceManager) {
        SeasonalStickerConfig server = getActiveServerConfig(preferenceManager);
        if (server != null && server.getId() != null && !server.getId().isEmpty()) {
            return server.getId();
        }
        LocalSeason local = resolveLocalSeason();
        return local != null ? local.id : null;
    }

    public static SeasonalStickerConfig parseConfig(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return new Gson().fromJson(json, SeasonalStickerConfig.class);
        } catch (JsonSyntaxException ignored) {
            return null;
        }
    }

    private static SeasonalStickerConfig getActiveServerConfig(PreferenceManager preferenceManager) {
        if (preferenceManager == null) {
            return null;
        }
        SeasonalStickerConfig config = parseConfig(preferenceManager.getString(Constant.SEASONAL_STICKER_JSON));
        if (config == null || !config.isEnabled()) {
            return null;
        }
        if (!isWithinDates(config.getStart(), config.getEnd())) {
            return null;
        }
        return config;
    }

    static boolean isWithinDates(String start, String end) {
        DATE_FORMAT.setTimeZone(TimeZone.getDefault());
        try {
            Date startDate = parseDateStart(start);
            Date endDate = parseDateEnd(end);
            if (startDate == null && endDate == null) {
                return true;
            }
            long now = System.currentTimeMillis();
            if (startDate != null && now < startDate.getTime()) {
                return false;
            }
            if (endDate != null && now > endDate.getTime()) {
                return false;
            }
            return true;
        } catch (ParseException ignored) {
            return true;
        }
    }

    private static Date parseDateStart(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return DATE_FORMAT.parse(value.trim());
    }

    private static Date parseDateEnd(String value) throws ParseException {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(DATE_FORMAT.parse(value.trim()));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    private static LocalSeason resolveLocalSeason() {
        Calendar calendar = Calendar.getInstance();
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        if (month == Calendar.MARCH && day >= 1 && day <= 20) {
            return LocalSeason.HOLI;
        }
        if (month == Calendar.OCTOBER || (month == Calendar.NOVEMBER && day <= 15)) {
            return LocalSeason.DIWALI;
        }
        if ((month == Calendar.DECEMBER && day >= 26) || (month == Calendar.JANUARY && day <= 5)) {
            return LocalSeason.NEW_YEAR;
        }
        return null;
    }

    private enum LocalSeason {
        DIWALI("diwali_local", R.string.sticker_prompt_diwali),
        HOLI("holi_local", R.string.sticker_prompt_holi),
        NEW_YEAR("new_year_local", R.string.sticker_prompt_new_year);

        final String id;
        final int promptRes;

        LocalSeason(String id, int promptRes) {
            this.id = id;
            this.promptRes = promptRes;
        }
    }
}
