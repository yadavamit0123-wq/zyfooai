package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

/**
 * One wallet ledger row — referral credit or subscription debit.
 */
public class WalletHistoryItem {

    @SerializedName(value = "id", alternate = {"txn_id", "transaction_id"})
    public String id;

    /** credit | debit */
    @SerializedName(value = "type", alternate = {"txn_type", "entry_type"})
    public String type;

    @SerializedName(value = "amount", alternate = {"value", "earning", "credit_amount", "debit_amount"})
    public String amount;

    @SerializedName(value = "title", alternate = {"label", "description", "note", "remark"})
    public String title;

    @SerializedName(value = "from_user", alternate = {"fromUser", "user_name", "name", "referred_user", "referrer_name"})
    public String fromUser;

    @SerializedName(value = "from_user_id", alternate = {"fromUserId", "user_id", "referred_user_id"})
    public String fromUserId;

    @SerializedName(value = "plan_name", alternate = {"planName", "package_name"})
    public String planName;

    @SerializedName(value = "created_at", alternate = {"createdAt", "datetime", "date_time", "date", "timestamp"})
    public String createdAt;

    public boolean isCredit() {
        if (type == null) {
            return true;
        }
        String t = type.trim().toLowerCase();
        return t.contains("credit") || t.equals("cr") || t.equals("+") || t.equals("in");
    }

    public String displayAmount() {
        return amount != null ? amount.trim() : "0";
    }

    public String displayWhoOrWhat() {
        if (fromUser != null && !fromUser.trim().isEmpty()) {
            return fromUser.trim();
        }
        if (planName != null && !planName.trim().isEmpty()) {
            return planName.trim();
        }
        if (title != null && !title.trim().isEmpty()) {
            return title.trim();
        }
        return isCredit() ? "Referral" : "Subscription";
    }

    public String displayDateTime() {
        return createdAt != null ? createdAt.trim() : "";
    }
}
