package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddProductsAdminController {
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
    private ChoiceBox<String> productType;

    public void initialize() {
        productType.getItems().addAll("Samsung", "iPhone", "Nokia","Opppo","Xiaomi");
        productType.getSelectionModel().selectFirst();
    }
    public void addProduct(ActionEvent event) {
        if(productImageUrl.getText().isEmpty() || productName.getText().isEmpty() || productPrice.getText().isEmpty() || productQuantity.getText().isEmpty() || productDescription.getText().isEmpty()){
            ReUse.showAlert("Không được để trống");
            return;
        }
        try {
            String productImage1 = this.productImageUrl.getText();
            String productName = this.productName.getText();
            String priceString = this.productPrice.getText().replace(" vnđ" , "").trim();
            BigDecimal productPrice = new BigDecimal(priceString);

            int productQuantity = Integer.parseInt(this.productQuantity.getText());
            String productDescription = this.productDescription.getText();
            String productType = this.productType.getValue().toString();

            String query = "INSERT INTO Products(productImage, productName, price,quantity, description,type) VALUES (?,?,?,?,?,?)";
            try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setString(1, productImage1);
                ps.setString(2, productName);
                ps.setBigDecimal(3, productPrice);
                ps.setInt(4, productQuantity);
                ps.setString(5, productDescription);
                ps.setString(6, productType);
                ps.executeUpdate();
                System.out.println("Thêm thành công sản phẩm mới");
                Stage stage = (Stage) productImageUrl.getScene().getWindow();
                stage.close();
            }
        }catch (NumberFormatException| SQLException e){
            ReUse.showAlert("Sai định dạng đầu vào ");
        }
    }

    public void afterEnterUrl(KeyEvent keyEvent) {
        String newUrl = productImageUrl.getText();
        try{
            Image image = new Image(newUrl,true);
            productImage.setImage(image);
        }catch (Exception e){
            System.out.println("Không thể tải ảnh " + e.getMessage());
            ReUse.showAlert("Không thể tải ảnh");
        }
    }
}
