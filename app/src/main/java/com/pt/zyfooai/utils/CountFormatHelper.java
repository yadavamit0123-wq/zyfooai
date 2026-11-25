package com.pt.zyfooai.utils;

/**
 * Engagement counts: 1→1+, 999→999+, 1000→1k+, 2000→2k+, 1_000_000→1M+.
 */
public final class CountFormatHelper {

    private CountFormatHelper() {
    }

    public static String formatPlus(long count) {
        if (count < 0) {
            count = 0;
        }
        if (count < 1000L) {
            return count + "+";
        }
        if (count < 1_000_000L) {
            return (count / 1000L) + "k+";
        }
        if (count < 1_000_000_000L) {
            return (count / 1_000_000L) + "M+";
        }
        return (count / 1_000_000_000L) + "B+";
    }
}
