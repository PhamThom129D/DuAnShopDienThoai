package com.example.duanshopdienthoai.Customer.Invoice;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    ObservableList<ProductOrder> productList = FXCollections.observableArrayList();
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
            productList.clear();

            while(resultSet.next()){
                        String productName=resultSet.getString("productName");
                        int quantity=resultSet.getInt("quantity");
                        BigDecimal amountPrice =resultSet.getBigDecimal("amountPrice");
                        productList.add(new ProductOrder(productName, quantity, amountPrice));
            }
            TableView productTable = new TableView();
            productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            productTable.setItems(productList);

            TableColumn productNameCol = new TableColumn("Tên Sản Phẩm");
            TableColumn quantityCol = new TableColumn("Số lượng");
            TableColumn amountCol = new TableColumn("Thành Tiền");

            productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amountPrice"));


            productTable.getColumns().addAll(productNameCol, quantityCol, amountCol);
            productTable.setItems(productList);
            productVbox.getChildren().clear();
            productVbox.getChildren().add(productTable);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void showOrders(int userID) {
        String query = "SELECT distinct o.orderID, i.invoiceID, o.orderDate, i.paymentDate, i.paymentMethod, i.total AS totalPrice, o.status\n" +
                "FROM `Order` o \n" +
                "JOIN Cart_Order co ON o.orderID = co.orderID \n" +
                "JOIN Cart c ON co.cartID = c.cartID \n" +
                "JOIN Invoices i ON o.orderID = i.orderID \n" +
                "WHERE o.userID = ?  order by i.paymentDate DESC ;";

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
        Label totalPriceLabel = new Label("Tổng tiền: " + totalPrice + " vnđ");
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
        Main.changeScene("Customer/HomeCustomer.fxml");
    }
}
