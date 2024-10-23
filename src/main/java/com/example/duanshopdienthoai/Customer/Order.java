package com.example.duanshopdienthoai.Customer;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.math.BigDecimal;

public class Order {
    private final StringProperty name;
    private final SimpleObjectProperty<BigDecimal> price;
    private final IntegerProperty quantity;
    private final SimpleObjectProperty<BigDecimal> amount;

    public Order(String name, BigDecimal price, int quantity) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.amount = new SimpleObjectProperty<>(price.multiply(BigDecimal.valueOf(quantity))); // Tính tổng giá
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.get();
    }

    public SimpleObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public BigDecimal getAmount() {
        return amount.get();
    }

    public SimpleObjectProperty<BigDecimal> amountProperty() {
        return amount;
    }
}
