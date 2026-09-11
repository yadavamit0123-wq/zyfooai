package com.growwthapps.dailypost.v2.model;

public class OrderRequest {
    private String name;
    private String email;
    private String phone;
    private String amount;
    private String description;

    public OrderRequest(String name, String email, String phone, String amount, String description) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.amount = amount;
        this.description = description;
    }
}