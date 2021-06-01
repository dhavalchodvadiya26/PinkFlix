package com.example.item;

import java.io.Serializable;

public class ItemSubscription implements Serializable {
    private String transactionId;
    private String transction_user_id;
    private String transction_email;
    private String transction_plan_id;
    private String transction_gateway;
    private String transction_payment_amount;
    private String transction_payment_id;
    private String transction_promocode;
    private String transction_donate_flag;
    private String transction_date;
    private String transction_plan_name;
    private String movie_name;
    private String expirty_date;
    private String transction_expiry_date;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransction_user_id() {
        return transction_user_id;
    }

    public void setTransction_user_id(String transction_user_id) {
        this.transction_user_id = transction_user_id;
    }

    public String getTransction_email() {
        return transction_email;
    }

    public void setTransction_email(String transction_email) {
        this.transction_email = transction_email;
    }

    public String getTransction_plan_id() {
        return transction_plan_id;
    }

    public void setTransction_plan_id(String transction_plan_id) {
        this.transction_plan_id = transction_plan_id;
    }

    public String getTransction_gateway() {
        return transction_gateway;
    }

    public void setTransction_gateway(String transction_gateway) {
        this.transction_gateway = transction_gateway;
    }

    public String getTransction_payment_amount() {
        return transction_payment_amount;
    }

    public void setTransction_payment_amount(String transction_payment_amount) {
        this.transction_payment_amount = transction_payment_amount;
    }

    public String getTransction_payment_id() {
        return transction_payment_id;
    }

    public void setTransction_payment_id(String transction_payment_id) {
        this.transction_payment_id = transction_payment_id;
    }

    public String getTransction_promocode() {
        return transction_promocode;
    }

    public void setTransction_promocode(String transction_promocode) {
        this.transction_promocode = transction_promocode;
    }

    public String getTransction_donate_flag() {
        return transction_donate_flag;
    }

    public void setTransction_donate_flag(String transction_donate_flag) {
        this.transction_donate_flag = transction_donate_flag;
    }

    public String getTransction_date() {
        return transction_date;
    }

    public void setTransction_date(String transction_date) {
        this.transction_date = transction_date;
    }

    public String getTransction_plan_name() {
        return transction_plan_name;
    }

    public void setTransction_plan_name(String transction_plan_name) {
        this.transction_plan_name = transction_plan_name;
    }

    public String getMovie_name() {
        return movie_name;
    }

    public void setMovie_name(String movie_name) {
        this.movie_name = movie_name;
    }

    public String getExpirty_date() {
        return expirty_date;
    }

    public String getTransction_expiry_date() {
        return transction_expiry_date;
    }

    public void setTransction_expiry_date(String transction_expiry_date) {
        this.transction_expiry_date = transction_expiry_date;
    }

    public void setExpirty_date(String expirty_date) {
        this.expirty_date = expirty_date;
    }
}
