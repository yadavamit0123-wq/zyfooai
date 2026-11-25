package com.pt.zyfooai.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WalletHistoryResponse {

    @SerializedName("success")
    public Object successRaw;

    @SerializedName(value = "wallet_balance", alternate = {"walletBalance", "balance"})
    public String walletBalance;

    @SerializedName(value = "data", alternate = {"history", "transactions", "items"})
    public List<WalletHistoryItem> data;

    @SerializedName("message")
    public String message;

    public boolean isSuccess() {
        if (successRaw == null) {
            return data != null;
        }
        if (successRaw instanceof Boolean) {
            return (Boolean) successRaw;
        }
        String s = String.valueOf(successRaw).trim().toLowerCase();
        return "1".equals(s) || "true".equals(s) || "yes".equals(s);
    }
}
