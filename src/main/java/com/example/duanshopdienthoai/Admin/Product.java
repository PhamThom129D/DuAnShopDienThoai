package com.example.duanshopdienthoai.Admin;

import java.math.BigDecimal;

public class Product {
    int IDProducts;
    String ImageProducts;
    String NameProducts;
    int QuantityProducts;
    BigDecimal PriceProducts;
    String TypeProducts;
    String DescriptionProducts;
    Boolean StateProducts;

    public Product(int IDProducts, String ImageProducts , String NameProducts , int QuantityProducts , BigDecimal PriceProducts , String TypeProducts , String DescriptionProducts , boolean StateProducts){
        this.IDProducts = IDProducts;
        this.ImageProducts = ImageProducts;
        this.NameProducts = NameProducts;
        this.QuantityProducts = QuantityProducts;
        this.PriceProducts = PriceProducts;
        this.TypeProducts = TypeProducts;
        this.DescriptionProducts = DescriptionProducts;
        this.StateProducts = StateProducts;
    }

    public int getIDProducts() {
        return IDProducts;
    }

    public void setIDProducts(int IDProducts) {
        this.IDProducts = IDProducts;
    }

    public String getImageProducts() {
        return ImageProducts;
    }

    public void setImageProducts(String imageProducts) {
        ImageProducts = imageProducts;
    }

    public String getNameProducts() {
        return NameProducts;
    }

    public void setNameProducts(String nameProducts) {
        NameProducts = nameProducts;
    }

    public int getQuantityProducts() {
        return QuantityProducts;
    }

    public void setQuantityProducts(int quantityProducts) {
        QuantityProducts = quantityProducts;
    }

    public BigDecimal getPriceProducts() {
        return PriceProducts;
    }

    public void setPriceProducts(BigDecimal priceProducts) {
        PriceProducts = priceProducts;
    }

    public String getTypeProducts() {
        return TypeProducts;
    }

    public void setTypeProducts(String typeProducts) {
        TypeProducts = typeProducts;
    }

    public String getDescriptionProducts() {
        return DescriptionProducts;
    }

    public void setDescriptionProducts(String descriptionProducts) {
        DescriptionProducts = descriptionProducts;
    }

    public Boolean getStateProducts() {
        return StateProducts;
    }

    public void setStateProducts(Boolean stateProducts) {
        StateProducts = stateProducts;
    }
}
