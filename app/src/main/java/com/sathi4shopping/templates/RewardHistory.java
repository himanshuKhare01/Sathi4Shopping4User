package com.sathi4shopping.templates;

public class RewardHistory {
    private String description;
    private String change;
    private String amount;
    private String time;

    public RewardHistory() {
    }

    public RewardHistory(String description, String change, String amount,String time) {
        this.description = description;
        this.amount = amount;
        this.change=change;
        this.time=time;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public void setChange(String change) {
        this.change = change;
    }

    public String getChange() {
        return change;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTime() {
        return time;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
