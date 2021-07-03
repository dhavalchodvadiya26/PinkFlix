package com.example.itemmodels;

public class ItemPaymentSetting {

    private String currencyCode;
    private String payPalClientId;
    private String stripePublisherKey;
    private String razorPayKey;
    private boolean isPayPal = false;
    private boolean isPayPalSandbox = false;
    private boolean isStripe = false;
    private boolean isRazorPay = false;

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isPayPal() {
        return isPayPal;
    }

    public void setPayPal(boolean payPal) {
        isPayPal = payPal;
    }

    public boolean isStripe() {
        return isStripe;
    }

    public void setStripe(boolean stripe) {
        isStripe = stripe;
    }

    public boolean isRazorPay() {
        return isRazorPay;
    }

    public void setRazorPay(boolean razorPay) {
        isRazorPay = razorPay;
    }


    public boolean isPayPalSandbox() {
        return isPayPalSandbox;
    }

    public void setPayPalSandbox(boolean payPalSandbox) {
        isPayPalSandbox = payPalSandbox;
    }

    public String getPayPalClientId() {
        return payPalClientId;
    }

    public void setPayPalClientId(String payPalClientId) {
        this.payPalClientId = payPalClientId;
    }

    public String getStripePublisherKey() {
        return stripePublisherKey;
    }

    public void setStripePublisherKey(String stripePublisherKey) {
        this.stripePublisherKey = stripePublisherKey;
    }

    public String getRazorPayKey() {
        return razorPayKey;
    }

    public void setRazorPayKey(String razorPayKey) {
        this.razorPayKey = razorPayKey;
    }
}
