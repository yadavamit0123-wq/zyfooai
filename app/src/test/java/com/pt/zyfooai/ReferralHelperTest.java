package com.pt.zyfooai;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import com.pt.zyfooai.utils.ReferralHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 33)
public class ReferralHelperTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
    }

    @Test
    public void referralCode_isStable() {
        String first = ReferralHelper.getOrCreateReferralCode(context);
        String second = ReferralHelper.getOrCreateReferralCode(context);
        assertEquals(first, second);
        assertTrue(first.startsWith("ZY"));
    }

    @Test
    public void applyReferral_incrementsCount() {
        int before = ReferralHelper.getReferralCount(context);
        ReferralHelper.applyReferralCode(context, "ZY999999");
        assertEquals(before + 1, ReferralHelper.getReferralCount(context));
    }

    @Test
    public void selfReferral_isIgnored() {
        int before = ReferralHelper.getReferralCount(context);
        String code = ReferralHelper.getOrCreateReferralCode(context);
        ReferralHelper.applyReferralCode(context, code);
        assertEquals(before, ReferralHelper.getReferralCount(context));
    }
}
