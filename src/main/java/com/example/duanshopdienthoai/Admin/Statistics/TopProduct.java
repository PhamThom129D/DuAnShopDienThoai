package com.example.duanshopdienthoai.Admin.Statistics;

public class TopProduct {
    private String productName;
    private int totalSold;

    public TopProduct(String productName, int totalSold) {
        this.productName = productName;
        this.totalSold = totalSold;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getTotalSold() {
        return totalSold;
    }

    public void setTotalSold(int totalSold) {
        this.totalSold = totalSold;
    }
}
