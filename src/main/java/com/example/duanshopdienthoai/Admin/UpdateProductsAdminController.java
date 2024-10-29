package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateProductsAdminController {
    @FXML
    private TextField productImage;
    @FXML
    private TextField productName;
    @FXML
    private TextField productPrice;
    @FXML
    private TextField productQuantity;
    @FXML
    private TextField productDescription;
    @FXML
    private ChoiceBox typeProduct;
    @FXML
    private RadioButton InStock;
    @FXML
    private RadioButton OutOfStock;

    private Product product;

    public void initialize() {
        typeProduct.getItems().addAll("Samsung", "iPhone", "Nokia","Opppo","Xiaomi");
        typeProduct.getSelectionModel().selectFirst();
        typeProduct.getSelectionModel().selectedItemProperty().addListener((observableValue, oldvalue, newValue) -> {
                typeProduct.getValue().toString();
        });
        ToggleGroup stockGroup = new ToggleGroup();
        InStock.setToggleGroup(stockGroup);
        OutOfStock.setToggleGroup(stockGroup);
    }
    public void setProducts(Product product) {
        this.product = product;
        productImage.setText(product.getImageProducts());
        productName.setText(product.getNameProducts());
        productPrice.setText(product.getPriceProducts().toString());
        productQuantity.setText(String.valueOf(product.getQuantityProducts()));
        productDescription.setText(product.getDescriptionProducts());
        if(product.getStateProducts()){
            InStock.setSelected(true);
        }else {
            OutOfStock.setSelected(true);
        }
    }

    public void updateProduct(ActionEvent event) {
        if(product == null){
            ReUse.showAlert("Không tìm thấy sản phẩm để cập nhật.");
        }else if(productImage.getText().isEmpty() || productName.getText().isEmpty() || productPrice.getText().isEmpty() || productQuantity.getText().isEmpty() || productDescription.getText().isEmpty()){
            ReUse.showAlert("Không được để trống.");
        }
        try{
            product.setImageProducts(productImage.getText());
            product.setNameProducts(productName.getText());
            product.setPriceProducts(new BigDecimal(productPrice.getText()));
            product.setQuantityProducts(Integer.parseInt(productQuantity.getText()));
            product.setDescriptionProducts(productDescription.getText());
            product.setTypeProducts((String) typeProduct.getValue());
            product.setStateProducts(InStock.isSelected());
            
            saveProduct(product);
        }catch (NumberFormatException | SQLException e){
            System.out.println("Sai định dạng đầu vào");
        }
    }

    private void saveProduct(Product product) throws SQLException {
        String query = "Update Products set productImage = ? , productName = ? , quantity = ? , price = ? , type = ? , description =? , stock =? where productID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, product.getImageProducts());
            ps.setString(2, product.getNameProducts());
            ps.setInt(3, product.getQuantityProducts());
            ps.setBigDecimal(4, product.getPriceProducts());
            ps.setString(5,product.getTypeProducts());
            ps.setString(6, product.getDescriptionProducts());
            ps.setBoolean(7, product.getStateProducts());
            ps.setInt(8,product.getIDProducts());
            ps.executeUpdate();

            Stage stage = (Stage) productImage.getScene().getWindow();
            stage.close();
        }catch (SQLException e){
            e.printStackTrace();
            ReUse.showError("Error","Không thể cập nhật thông tin " + e.getMessage());
        }
    }

}
