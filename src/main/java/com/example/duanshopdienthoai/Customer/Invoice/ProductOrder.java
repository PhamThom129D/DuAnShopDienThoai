package com.example.duanshopdienthoai.Customer.Invoice;

import java.math.BigDecimal;

public class ProductOrder {
    private String productName;
    private int quantity;
    private BigDecimal amountPrice;

    public ProductOrder(String productName, int quantity, BigDecimal amountPrice) {
        this.productName = productName;
        this.quantity = quantity;
        this.amountPrice = amountPrice;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getAmountPrice() {
        return amountPrice;
    }
}
