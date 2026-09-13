package com.pt.zyfooai;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.pt.zyfooai.model.PostItem;
import com.pt.zyfooai.utils.OfflinePostsCache;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class OfflinePostsCacheTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void saveAndLoad_postsRoundTrip() {
        PostItem item = new PostItem("101", "", "image", "https://example.com/a.png", "en", false, false, false);
        List<PostItem> posts = Arrays.asList(item);
        OfflinePostsCache.save(context, posts);
        List<PostItem> loaded = OfflinePostsCache.load(context);
        assertFalse(loaded.isEmpty());
        assertTrue(loaded.get(0).postId.equals("101"));
    }
}
