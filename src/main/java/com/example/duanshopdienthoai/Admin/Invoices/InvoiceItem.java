package com.example.duanshopdienthoai.Admin.Invoices;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class InvoiceItem {
    private int invoiceID;
    private int orderID;
    private Timestamp orderDate;
    private Timestamp paymentDate;
    private String paymentMethod;
    private BigDecimal totalPrice;
    private String paid;

    public InvoiceItem(int invoiceID, int orderID, Timestamp orderDate, Timestamp paymentDate, String paymentMethod, BigDecimal totalPrice, String paid) {
        this.invoiceID = invoiceID;
        this.orderID = orderID;
        this.orderDate = orderDate;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.totalPrice = totalPrice;
        this.paid = paid;
    }

    public int getInvoiceID() {
        return invoiceID;
    }

    public void setInvoiceID(int invoiceID) {
        this.invoiceID = invoiceID;
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public Timestamp getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Timestamp paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getPaid() {
        return paid;
    }

    public void setPaid(String paid) {
        this.paid = paid;
    }
}