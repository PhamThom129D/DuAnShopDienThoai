package com.example.duanshopdienthoai.Customer.Invoice;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class InvoiceController {

    @FXML
    private VBox waitVBox;
    @FXML
    private VBox confirmVBox;

    public void initialize() {
        int userID = LoggedInUser.getInstance().getUserID();
        showOrders(userID);

    }
    private void showProductFromOrder(int orderID , int userID, VBox productVbox) {
        String query = "SELECT o.status,p.productName,co.quantity, p.price * co.quantity AS amountPrice " +
                "        FROM `Order` o" +
                "        JOIN Cart_Order co ON o.orderID = co.orderID " +
                "        JOIN Cart c ON co.cartID = c.cartID " +
                "        JOIN Products p ON c.productID = p.productID " +
                "        WHERE o.userID = ? and o.orderID = ?";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            preparedStatement.setInt(2, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();
            productVbox.getChildren().clear();
            while(resultSet.next()){
                        String productName=resultSet.getString("productName");
                        int quantity=resultSet.getInt("quantity");
                        BigDecimal amountPrice =resultSet.getBigDecimal("amountPrice");
                Label productLabel = new Label("Tên Sản Phẩm : " + productName +"\t Số lượng : " + quantity + "\t Thành Tiền : " + amountPrice );
                    productVbox.getChildren().add(productLabel);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showOrders(int userID) {
        String query = "SELECT o.orderID, i.invoiceID, o.orderDate, i.paymentDate, i.paymentMethod, i.total AS totalPrice,p.productName ,  " +
                "o.status " +
                "FROM `Order` o " +
                "JOIN Cart_Order co ON o.orderID = co.orderID " +
                "JOIN Cart c ON co.cartID = c.cartID " +
                "JOIN Products p ON p.productID = c.productID " +
                "JOIN Invoices i ON o.orderID = i.orderID " +
                "WHERE o.userID = ? order by i.paymentDate DESC ";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                VBox vBox = createOrderVBox(
                        resultSet.getInt("invoiceID"),
                        resultSet.getInt("orderID"),
                        resultSet.getTimestamp("orderDate"),
                        resultSet.getTimestamp("paymentDate"),
                        resultSet.getString("paymentMethod"),
                        resultSet.getBigDecimal("totalPrice")
                );

                Boolean status = resultSet.getBoolean("status");
                if (!status) {
                    waitVBox.getChildren().add(vBox);
                } else {
                    confirmVBox.getChildren().add(vBox);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private VBox createOrderVBox(int invoiceID, int orderID, Timestamp orderDate, Timestamp paymentDate,
                                 String paymentMethod, BigDecimal totalPrice) {

        VBox vBox = new VBox(10);
        vBox.setAlignment(Pos.TOP_LEFT);
        vBox.getStyleClass().add("vbox-order");

        Label invoiceIDLabel = new Label("Mã hóa đơn: " + invoiceID);
        Label orderIDLabel = new Label("Mã đơn hàng: " + orderID);
        Label orderDateLabel = new Label("Ngày đặt hàng: " + orderDate);
        Label paymentDateLabel = new Label("Ngày thanh toán: " + paymentDate);
        Label paymentMethodLabel = new Label("Phương thức thanh toán: " + paymentMethod);
        Label totalPriceLabel = new Label("Tổng tiền: " + totalPrice);
        Button productButton = new Button("Hiển thị sản phẩm");
        VBox productVBox = new VBox(10);
        productVBox.setAlignment(Pos.TOP_LEFT);
        productButton.setOnAction(event -> {
            if(!productVBox.isVisible()){
                productVBox.setVisible(true);
                showProductFromOrder(orderID,LoggedInUser.getInstance().getUserID(),productVBox);
                productButton.setText("Ẩn sản phẩm");
            }else {
                productVBox.setVisible(false);
                productVBox.getChildren().clear();
                productButton.setText("Hiển thị sản phẩm");
            }
        });
        vBox.getChildren().addAll(invoiceIDLabel, orderIDLabel, orderDateLabel, paymentDateLabel, paymentMethodLabel,  totalPriceLabel, productButton,productVBox);

        return vBox;
    }

    public void goBack() throws IOException {
        Main.changeScene("HomeCustomer.fxml");
    }
}
