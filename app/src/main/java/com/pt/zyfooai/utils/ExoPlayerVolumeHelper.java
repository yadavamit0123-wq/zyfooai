package com.pt.zyfooai.utils;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.SimpleExoPlayer;

/**
 * ExoPlayer 2.10: {@code setVolume} lives on {@link SimpleExoPlayer}, not the ExoPlayer interface.
 */
public final class ExoPlayerVolumeHelper {

    private ExoPlayerVolumeHelper() {
    }

    public static void setVolume(ExoPlayer player, float volume) {
        if (player instanceof SimpleExoPlayer) {
            ((SimpleExoPlayer) player).setVolume(volume);
        }
    }
}
