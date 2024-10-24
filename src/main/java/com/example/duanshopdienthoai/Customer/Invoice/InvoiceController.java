package com.example.duanshopdienthoai.Customer.Invoice;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.*;

public class InvoiceController {
    @FXML
    private Tab waitTab;
    @FXML
    private Tab confirmTab;

    public void initialize() {
        int userID = LoggedInUser.getInstance().getUserID();
        showOrders(userID);
    }
    private void showProductFromOrder(int orderID) {
        String query = "Select p.productName , co.quantity , p.price * co.quantity AS amountPrice"+
                "FROM products p JOIN Cart co ON ";
    }
    private void showOrders(int userID) {
        String query = "SELECT o.orderID, i.invoiceID, o.orderDate, i.paymentDate, i.paymentMethod, i.total AS totalPrice,p.productName , co.quantity , p.price * co.quantity AS amountPrice,  " +
                "o.status " +
                "FROM `Order` o " +
                "JOIN Cart_Order co ON o.orderID = co.orderID " +
                "JOIN Cart c ON co.cartID = c.cartID " +
                "JOIN Products p ON p.productID = c.productID " +
                "JOIN Invoices i ON o.orderID = i.orderID " +
                "WHERE o.userID = ?";

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
                HBox hBox = createOrderHBox(
                        resultSet.getString("productName"),
                        resultSet.getInt("quantity"),
                        resultSet.getBigDecimal("amountPrice")
                        );

                Boolean status = resultSet.getBoolean("status");
                if (status) {
                    ((VBox) ((ScrollPane) confirmTab.getContent()).getContent()).getChildren().add(vBox);
                } else {
                    ((VBox) ((ScrollPane) waitTab.getContent()).getContent()).getChildren().add(vBox);
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

//        HBox hBox = createOrderHBox();
        vBox.getChildren().addAll(invoiceIDLabel, orderIDLabel, orderDateLabel, paymentDateLabel, paymentMethodLabel,  totalPriceLabel);

        return vBox;
    }
    private HBox createOrderHBox( String productName, int quantity, BigDecimal amount) {
        HBox hBox = new HBox(10);
        hBox.setAlignment(Pos.TOP_LEFT);
        hBox.getStyleClass().add("hbox-order");
        Label productNameLabel = new Label("Tên sản phẩm: " + productName);
        Label quantityLabel = new Label("Số lượng: " + quantity);
        Label amountLabel = new Label("Thành tiền: " + amount);
        hBox.getChildren().addAll(productNameLabel, quantityLabel, amountLabel);

        return hBox;
    }
}
