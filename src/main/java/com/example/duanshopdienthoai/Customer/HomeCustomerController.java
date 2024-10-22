package com.example.duanshopdienthoai.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.LoggedInUser;
import com.example.duanshopdienthoai.Test;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.*;

public class HomeCustomerController {
    @FXML
    private FlowPane topProduct;
    @FXML
    private FlowPane randomProduct;

    public HomeCustomerController() throws SQLException {
    }

    @FXML
    public void initialize() throws SQLException {
        loadProductFromDatabase(topProduct,queryTop5Product);
        loadProductFromDatabase(randomProduct,queryRandomProduct);
    }
    String queryTop5Product = "SELECT * FROM products ORDER BY quantity ASC limit 5";
    String queryRandomProduct = "SELECT * FROM products ORDER BY RAND() limit 15";
    Connection conn = DatabaseConnection.getConnection();
    Statement stmt = conn.createStatement();

    private  void loadProductFromDatabase(FlowPane pane,String query) throws SQLException {
        ResultSet rs = stmt.executeQuery(query);

        pane.setHgap(30);
        pane.setVgap(30);
        pane.setPrefWrapLength(600);

        while (rs.next()) {
            int productId = rs.getInt("productId");
                boolean stock = rs.getBoolean("stock");
                String imageUrl = rs.getString("productImage");
                Image productImage = new Image(imageUrl);
                String productName = rs.getString("productName");
                BigDecimal price = rs.getBigDecimal("price");
                int quantity = rs.getInt("quantity");
                String type = rs.getString("type");
                String description = rs.getString("description");

                VBox vbox = new VBox(10);
                vbox.setAlignment(Pos.CENTER);
                vbox.getStyleClass().add("top-product");

                ImageView imageView = new ImageView(productImage);
                imageView.setFitWidth(155);
                imageView.setFitHeight(145);

                Label nameLabel = new Label(productName);
                Label priceLabel = new Label("Price: " + price);
                Label quantityLabel = new Label("Quantity: " + quantity);

            if (stock) {
                Button buyButton = new Button("Buy");
                buyButton.setOnAction(event -> {
                    if(LoggedInUser.getInstance() != null){
                        int userID = LoggedInUser.getInstance().getUserID();
                        addProductToCart(String.valueOf(productId),userID,1,price);
                    }else {
                        System.out.println("Không tìm thấy người dùng đang đăng nhập");
                    }
                        });
                buyButton.getStyleClass().add("button");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, buyButton);
            } else {
                Label outOfStockLabel = new Label("Out of stock");
                outOfStockLabel.setStyle("-fx-text-fill: red");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, outOfStockLabel);
            }


            Tooltip tooltip = new Tooltip("Description: " + description);
                Tooltip.install(imageView, tooltip);
                imageView.setOnMouseEntered(event -> {
                    imageView.setScaleX(1.1);
                    imageView.setScaleY(1.1);
                });
                imageView.setOnMouseExited(event -> {
                    imageView.setScaleX(1.0);
                    imageView.setScaleY(1.0);
                });

                pane.getChildren().add(vbox);
            }
        }

    private void addProductToCart(String productID,int userID,int quantity,BigDecimal price)  {
        String query = "INSERT INTO Cart(productID, userID, quantity, price) VALUES (?, ?, ?, ?)";
        try{
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, productID);
            pstmt.setInt(2,userID);
            pstmt.setInt(3,quantity);
            pstmt.setBigDecimal(4,price);
            pstmt.executeUpdate();
            System.out.println("Thêm thành công");
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
