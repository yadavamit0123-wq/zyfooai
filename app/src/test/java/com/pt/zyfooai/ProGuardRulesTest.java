package com.pt.zyfooai;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ProGuardRulesTest {

    @Test
    public void proguardRules_containCriticalKeeps() throws IOException {
        String rules = Files.readString(Paths.get("proguard-rules.pro"));
        assertTrue(rules.contains("com.google.android.exoplayer2"));
        assertTrue(rules.contains("com.razorpay"));
        assertTrue(rules.contains("com.arthenica.mobileffmpeg"));
        assertTrue(rules.contains("com.pt.zyfooai.model"));
        assertTrue(rules.contains("androidx.room"));
        assertTrue(rules.contains("LineNumberTable"));
    }
}
