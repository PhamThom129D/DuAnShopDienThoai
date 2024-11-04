package com.example.duanshopdienthoai.Admin.Products;

import javafx.scene.image.ImageView;

import java.math.BigDecimal;

public class Product {
    int idProduct;
    ImageView imageProduct;
    String nameProduct;
    int quantityProduct;
    BigDecimal priceProduct;
    String typeProduct;
    String descriptionProduct;
    Boolean stateProduct;

    public Product(int idProduct, ImageView imageProduct , String nameProduct , int quantityProduct , BigDecimal priceProduct , String typeProduct , String descriptionProduct , boolean stateProduct){
        this.idProduct = idProduct;
        this.imageProduct = imageProduct;
        this.nameProduct = nameProduct;
        this.quantityProduct = quantityProduct;
        this.priceProduct = priceProduct;
        this.typeProduct = typeProduct;
        this.descriptionProduct = descriptionProduct;
        this.stateProduct = stateProduct;
    }

    public int getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(int idProduct) {
        this.idProduct = idProduct;
    }

    public ImageView getImageProduct() {
        return imageProduct;
    }

    public void setImageProduct(ImageView imageProducts) {
        imageProduct = imageProducts;
    }

    public String getNameProduct() {
        return nameProduct;
    }

    public void setNameProduct(String nameProducts) {
        nameProduct = nameProducts;
    }

    public int getQuantityProduct() {
        return quantityProduct;
    }

    public void setQuantityProduct(int quantityProducts) {
        quantityProduct = quantityProducts;
    }

    public BigDecimal getPriceProduct() {
        return priceProduct;
    }

    public void setPriceProduct(BigDecimal priceProducts) {
        priceProduct = priceProducts;
    }

    public String getTypeProduct() {
        return typeProduct;
    }

    public void setTypeProduct(String typeProducts) {
        typeProduct = typeProducts;
    }

    public String getDescriptionProduct() {
        return descriptionProduct;
    }

    public void setDescriptionProduct(String descriptionProducts) {
        descriptionProduct = descriptionProducts;
    }

    public Boolean getStateProduct() {
        return stateProduct;
    }

    public void setStateProduct(Boolean stateProducts) {
        stateProduct = stateProducts;
    }
}