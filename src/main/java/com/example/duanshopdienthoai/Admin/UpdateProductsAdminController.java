package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProductsAdminController {
    @FXML
    private ImageView productImage;
    @FXML
    private TextField productImageUrl;
    @FXML
    private TextField productName;
    @FXML
    private TextField productPrice;
    @FXML
    private TextField productQuantity;
    @FXML
    private TextArea productDescription;
    @FXML
    private ChoiceBox typeProduct;
    @FXML
    private RadioButton InStock;
    @FXML
    private RadioButton OutOfStock;

    private Product product;
private Map<String , Image> imageCache = new HashMap<>();
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
        productImage.setImage(product.getImageProduct().getImage());
        productImageUrl.setText(product.getImageProduct().getImage().getUrl());
        productName.setText(product.getNameProduct());
        productPrice.setText(product.getPriceProduct().toString() + " vnđ");
        productQuantity.setText(String.valueOf(product.getQuantityProduct()));
        productDescription.setText(product.getDescriptionProduct());
        if(product.getStateProduct()){
            InStock.setSelected(true);
        }else {
            OutOfStock.setSelected(true);
        }
    }

    public void updateProduct(ActionEvent event) {
        if(product == null){
            ReUse.showAlert("Không tìm thấy sản phẩm để cập nhật.");
        }else if(productImageUrl.getText().isEmpty() || productName.getText().isEmpty() || productPrice.getText().isEmpty() || productQuantity.getText().isEmpty() || productDescription.getText().isEmpty()){
            ReUse.showAlert("Không được để trống.");
        }
        try{
            Image image = new Image(productImageUrl.getText());
            product.getImageProduct().setImage(image);
            product.setNameProduct(productName.getText());
            String price = productPrice.getText().replace(" vnđ","").trim();
            product.setPriceProduct(new BigDecimal(price));
            product.setQuantityProduct(Integer.parseInt(productQuantity.getText()));
            product.setDescriptionProduct(productDescription.getText());
            product.setTypeProduct((String) typeProduct.getValue());
            product.setStateProduct(InStock.isSelected());

            saveProduct(product);
        }catch (NumberFormatException | SQLException e){
            System.out.println("Sai định dạng đầu vào");
        }
    }

    private void saveProduct(Product product) throws SQLException {
        String query = "Update Products set productImage = ? , productName = ? , quantity = ? , price = ? , type = ? , description =? , stock =? where productID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, product.getImageProduct().getImage().getUrl());
            ps.setString(2, product.getNameProduct());
            ps.setInt(3, product.getQuantityProduct());
            ps.setBigDecimal(4, product.getPriceProduct());
            ps.setString(5,product.getTypeProduct());
            ps.setString(6, product.getDescriptionProduct());
            ps.setBoolean(7, product.getStateProduct());
            ps.setInt(8,product.getIdProduct());
            ps.executeUpdate();

            Stage stage = (Stage) productImageUrl.getScene().getWindow();
            stage.close();
        }catch (SQLException e){
            e.printStackTrace();
            ReUse.showError("Error","Không thể cập nhật thông tin " + e.getMessage());
        }
    }

    public void afterEnterUrl(KeyEvent keyEvent) {
        String newUrl = productImageUrl.getText();
        try{
            Image image = new Image(newUrl,true);
            productImage.setImage(image);
        }catch (Exception e){
            System.out.println("Không thể tải ảnh " + e.getMessage());
        }

    }
}