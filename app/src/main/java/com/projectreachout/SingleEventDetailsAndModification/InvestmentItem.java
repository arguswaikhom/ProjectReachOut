package com.projectreachout.SingleEventDetailsAndModification;

public class InvestmentItem {

    private String investment_on;
    private double amount;

    public InvestmentItem() {
    }

    public InvestmentItem(String investment_on, double amount) {
        this.investment_on = investment_on;
        this.amount = amount;
    }

    public void setInvestment_on(String investment_on) {
        this.investment_on = investment_on;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getInvestment_on() {
        return investment_on;
    }

    public double getAmount() {
        return amount;
    }
}
