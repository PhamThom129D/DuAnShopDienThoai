package com.example.duanshopdienthoai.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HomeCustomerController {
    @FXML
    private FlowPane topProduct;
    @FXML
    private FlowPane randomProduct;
    @FXML
    private FlowPane showProduct;

    private Connection conn;

    public HomeCustomerController() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        conn = connection;
    }

    @FXML
    public void initialize() throws SQLException {
        loadProducts();
    }

    private void loadProducts() throws SQLException {
        String queryTop5Product = "SELECT * FROM products ORDER BY quantity ASC LIMIT 5";
        String queryRandomProduct = "SELECT * FROM products ORDER BY RAND() LIMIT 15";

        try (PreparedStatement pstmtTop = conn.prepareStatement(queryTop5Product);
             PreparedStatement pstmtRandom = conn.prepareStatement(queryRandomProduct);
             ResultSet rs1 = pstmtTop.executeQuery();
             ResultSet rs2 = pstmtRandom.executeQuery()) {

            loadProductFromDatabase(topProduct, rs1);
            loadProductFromDatabase(randomProduct, rs2);
        }
    }

    private void loadProductFromDatabase(FlowPane pane, ResultSet rs) throws SQLException {
        pane.setHgap(40);
        pane.setVgap(40);
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
            Label priceLabel = new Label("Giá: " + price);
            Label quantityLabel = new Label("Số lượng: " + quantity);

            if (stock) {
                Button buyButton = new Button("Thêm vào giỏ hàng");
                buyButton.setOnAction(event -> {
                    if (LoggedInUser.getInstance() != null) {
                        int userID = LoggedInUser.getInstance().getUserID();
                        addProductToCart(String.valueOf(productId), userID, 1, price);
                    } else {
                        System.out.println("Không tìm thấy người dùng đang đăng nhập");
                    }
                });
                buyButton.getStyleClass().add("button");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, quantityLabel, buyButton);
            } else {
                Label outOfStockLabel = new Label("Hết hàng");
                outOfStockLabel.setStyle("-fx-text-fill: red");
                vbox.getChildren().addAll(imageView, nameLabel, priceLabel, outOfStockLabel);
            }

            Tooltip tooltip = new Tooltip("Mô tả: " + description);
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

    private void addProductToCart(String productID, int userID, int quantity, BigDecimal price) {
        String query = "INSERT INTO Cart(productID, userID, quantity, price) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, productID);
            pstmt.setInt(2, userID);
            pstmt.setInt(3, quantity);
            pstmt.setBigDecimal(4, price);
            pstmt.executeUpdate();
            System.out.println("Thêm thành công");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField searchTextFile;

    public void searchProduct(ActionEvent event) {
        String searchText = searchTextFile.getText();
        showProduct.getChildren().clear();
        String querySearchProduct = "SELECT * FROM products WHERE productName LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(querySearchProduct)) {
            pstmt.setString(1, "%" + searchText + "%");
            try (ResultSet rs3 = pstmt.executeQuery()) {
                loadProductFromDatabase(showProduct, rs3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showHome(ActionEvent event) throws IOException, SQLException {
        Main.changeScene("HomeCustomer.fxml");
    }

    public void showProductType(String type){
        showProduct.getChildren().clear();
        String queryProductType = "SELECT * FROM products WHERE type = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(queryProductType)) {
            pstmt.setString(1, type);
            try (ResultSet rs4 = pstmt.executeQuery()) {
                loadProductFromDatabase(showProduct, rs4);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showIphone() {
        showProductType("Iphone");
    }

    public void showSamSung(ActionEvent event) {
        showProductType("Samsung");
    }

    public void showXiaomi(ActionEvent event) {
        showProductType("Xiaomi");
    }

    public void showOppo(ActionEvent event) {
        showProductType("Oppo");
    }

    public void showNokia(ActionEvent event) {
        showProductType("Nokia");
    }

    public void showAccount(MouseEvent mouseEvent) throws IOException {
        Main.changeScene("AccountCustomer.fxml");
    }

    public void showCart(MouseEvent mouseEvent) throws IOException {
        Main.changeScene("Cart.fxml");
    }
}
