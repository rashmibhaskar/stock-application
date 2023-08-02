package com.example.myapp.Portfolio;

public class Portfolio {
    private String ticker;
    private Integer shares;
    private Double amount;
    private Double changedprice;
    private Double marketValue;
    private Double changepercent;

    public Portfolio(String ticker, Integer shares, double amount, double changedprice, double changepercent,double marketValue){
        this.ticker=ticker;
        this.shares=shares;
        this.amount=amount;
        this.changedprice=changedprice;
        this.changepercent=changepercent;
        this.marketValue=marketValue;
    }

    public Double getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(Double marketValue) {
        this.marketValue = marketValue;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public Integer getShares() {
        return shares;
    }

    public void setShares(Integer shares) {
        this.shares = shares;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getChangedprice() {
        return changedprice;
    }

    public void setChangedprice(Double changedprice) {
        this.changedprice = changedprice;
    }

    public Double getChangepercent() {
        return changepercent;
    }

    public void setChangepercent(Double changepercent) {
        this.changepercent = changepercent;
    }

    @Override
    public String toString() {
        return "Portfolio{" +
                "ticker='" + ticker + '\'' +
                ", shares=" + shares +
                ", amount=" + amount +
                ", changedprice=" + changedprice +
                ", marketValue=" + marketValue +
                ", changepercent=" + changepercent +
                '}';
    }
}