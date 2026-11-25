package com.pt.zyfooai.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pt.zyfooai.model.WalletHistoryItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Local debit cache so subscription spends show in history even before server history syncs.
 */
public final class WalletLedgerPrefs {

    private static final String PREF = "zyfoo_wallet_ledger";
    private static final String KEY_DEBITS = "debits_json";
    private static final Gson GSON = new Gson();

    private WalletLedgerPrefs() {
    }

    public static void addDebit(Context context, WalletHistoryItem item) {
        if (context == null || item == null) {
            return;
        }
        List<WalletHistoryItem> list = getDebits(context);
        list.add(0, item);
        while (list.size() > 100) {
            list.remove(list.size() - 1);
        }
        prefs(context).edit().putString(KEY_DEBITS, GSON.toJson(list)).apply();
    }

    public static List<WalletHistoryItem> getDebits(Context context) {
        if (context == null) {
            return new ArrayList<>();
        }
        String raw = prefs(context).getString(KEY_DEBITS, "");
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        try {
            Type type = new TypeToken<List<WalletHistoryItem>>() {
            }.getType();
            List<WalletHistoryItem> parsed = GSON.fromJson(raw, type);
            return parsed != null ? new ArrayList<>(parsed) : new ArrayList<>();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public static List<WalletHistoryItem> mergeLocalDebits(
            Context context,
            List<WalletHistoryItem> serverItems
    ) {
        List<WalletHistoryItem> merged = new ArrayList<>();
        if (serverItems != null) {
            merged.addAll(serverItems);
        }
        List<WalletHistoryItem> local = getDebits(context);
        for (WalletHistoryItem debit : local) {
            if (debit == null) {
                continue;
            }
            if (!containsId(merged, debit.id)) {
                merged.add(debit);
            }
        }
        return merged;
    }

    private static boolean containsId(List<WalletHistoryItem> list, String id) {
        if (id == null || id.isEmpty()) {
            return false;
        }
        for (WalletHistoryItem item : list) {
            if (item != null && id.equals(item.id)) {
                return true;
            }
        }
        return false;
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
}
