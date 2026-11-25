package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

public class WalletPayResponse {

    @SerializedName("success")
    public Object successRaw;

    @SerializedName(value = "wallet_balance", alternate = {"walletBalance", "balance"})
    public String walletBalance;

    @SerializedName(value = "transaction_id", alternate = {"txn_id", "payment_id", "id"})
    public String transactionId;

    @SerializedName("message")
    public String message;

    @SerializedName("data")
    public WalletPayResponse data;

    public WalletPayResponse resolved() {
        return data != null && (transactionId == null || transactionId.isEmpty()) ? data : this;
    }

    public boolean isSuccess() {
        if (successRaw == null) {
            return transactionId != null && !transactionId.isEmpty();
        }
        if (successRaw instanceof Boolean) {
            return (Boolean) successRaw;
        }
        String s = String.valueOf(successRaw).trim().toLowerCase();
        return "1".equals(s) || "true".equals(s) || "yes".equals(s);
    }
}
