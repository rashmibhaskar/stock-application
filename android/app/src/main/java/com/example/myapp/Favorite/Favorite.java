package com.example.myapp.Favorite;

public class Favorite {
    private String ticker;
    private String companyName;
    private Double amount;
    private Double changedprice;
    private Double changepercent;

    public Favorite(String ticker, String companyName, Double amount,Double changedprice,Double changepercent) {
        this.ticker = ticker;
        this.companyName = companyName;
        this.amount = amount;
        this.changedprice=changedprice;
        this.changepercent=changepercent;

    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
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
        return "Favorite{" +
                "ticker='" + ticker + '\'' +
                ", companyName='" + companyName + '\'' +
                ", amount=" + amount +
                ", changedprice=" + changedprice +
                ", changepercent=" + changepercent +
                '}';
    }


}
