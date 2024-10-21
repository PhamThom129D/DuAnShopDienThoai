package com.example.duanshopdienthoai.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HomeCustomer {
    @FXML
    private FlowPane topProduct;

    @FXML
    public void initialize() throws SQLException {
        loadAllProduct();
    }

    private void loadAllProduct() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM products");

        topProduct.setHgap(30);
        topProduct.setVgap(30);
        topProduct.setPrefWrapLength(600);

        while (rs.next()) {
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
                buyButton.setOnAction(event -> showProductDetail(productImage, productName, price, description));
                buyButton.getStyleClass().add("button");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, buyButton);
            } else {
                Label outOfStockLabel = new Label("Out of stock");
                outOfStockLabel.setStyle("-fx-text-fill: red");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, outOfStockLabel);
            }

            Tooltip tooltip = new Tooltip("Name: " + productName + "\nPrice: " + price + "\nQuantity: " + quantity);
            Tooltip.install(imageView, tooltip);
            imageView.setOnMouseEntered(event -> {
                imageView.setScaleX(1.1);
                imageView.setScaleY(1.1);
            });
            imageView.setOnMouseExited(event -> {
                imageView.setScaleX(1.0);
                imageView.setScaleY(1.0);
            });

            topProduct.getChildren().add(vbox);
        }

        stmt.close();
        conn.close();
    }

    private void showProductDetail(Image productImage, String productName, BigDecimal price, String description) {
    }
}
