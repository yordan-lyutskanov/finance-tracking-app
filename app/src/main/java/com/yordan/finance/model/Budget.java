package com.yordan.finance.model;

public class Budget {
    private double currentAmount;
    private double maxAmount;
    private double progress;


    public Budget(double currentAmount, double maxAmount) {
        this.currentAmount = currentAmount;
        this.maxAmount = maxAmount;
        this.progress = maxAmount / currentAmount;
    }

    public double getCurrentAmount() {
        return currentAmount;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public double getProgress() {
        return progress;
    }

    public void incrementCurrentAmount(double inc){
        currentAmount += inc;
    }
}
