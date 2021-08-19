package com.projectreachout.Event.EventDetailsAndModification;

import com.google.gson.Gson;

public class InvestmentItem {

    private String investment_on = "";
    private String amount = "";

    public InvestmentItem() {
    }

    public InvestmentItem(String investment_on, String amount) {
        this.investment_on = investment_on;
        this.amount = amount;
    }

    public static InvestmentItem fromJson(String jsonString) {
        return new Gson().fromJson(jsonString, InvestmentItem.class);
    }

    public String getInvestment_on() {
        return investment_on;
    }

    public String getAmount() {
        return amount;
    }
}
