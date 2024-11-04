package com.example.duanshopdienthoai.Admin.Invoices;

import java.math.BigDecimal;

public class ProductInvoicesItem {
    private String productName;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;

    public ProductInvoicesItem(String productName, int quantity, BigDecimal price, BigDecimal amount) {
        this.productName = productName;
        this.quantity = quantity;
        this.price = price;
        this.amount = amount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
