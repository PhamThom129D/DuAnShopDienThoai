package com.example.duanshopdienthoai.Customer.Cart;

import javafx.beans.property.*;

import java.math.BigDecimal;

public class CartItem {
    private final StringProperty name;
    private final ObjectProperty<BigDecimal> price;
    private final IntegerProperty quantity;
    private final BooleanProperty checkbox;
    private final IntegerProperty cartID;


    public CartItem(String name, BigDecimal price, int quantity, boolean checkbox,int cartID) {
        this.name = new SimpleStringProperty(name);
        this.price = new SimpleObjectProperty<>(price);
        this.quantity = new SimpleIntegerProperty(quantity);
        this.checkbox = new SimpleBooleanProperty(checkbox);
        this.cartID = new SimpleIntegerProperty(cartID);

    }


    public int getCartID() {
        return cartID.get();
    }

    public IntegerProperty cartIDProperty() {
        return cartID;
    }

    public boolean isCheckbox() {
        return checkbox.get();
    }

    public BooleanProperty checkboxProperty() {
        return checkbox;
    }

    public void setCheckbox(boolean checkbox) {
        this.checkbox.set(checkbox);
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

    public ObjectProperty<BigDecimal> priceProperty() {
        return price;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity.set(quantity);
    }
}
