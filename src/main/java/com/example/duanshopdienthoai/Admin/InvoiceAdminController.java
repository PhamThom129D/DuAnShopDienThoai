package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.Customer.Invoice.ProductOrder;
import com.example.duanshopdienthoai.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.sql.*;

public class InvoiceAdminController {
    @FXML
    private VBox invoicesAdmin;
    ObservableList<ProductOrder> productList = FXCollections.observableArrayList();
    public void initialize() {

        showOrders();
    }
    private void showProductFromOrder(int orderID, VBox productVbox) {
        String query = "SELECT o.status, p.productName, co.quantity, p.price * co.quantity AS amountPrice " +
                "FROM `Order` o " +
                "JOIN Cart_Order co ON o.orderID = co.orderID " +
                "JOIN Cart c ON co.cartID = c.cartID " +
                "JOIN Products p ON c.productID = p.productID " +
                "WHERE o.orderID = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();
            productList.clear();

            while (resultSet.next()) {
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                BigDecimal amountPrice = resultSet.getBigDecimal("amountPrice");
                productList.add(new ProductOrder(productName, quantity, amountPrice));
            }

            TableView<ProductOrder> productTable = new TableView<>();
            productTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            productTable.setItems(productList);

            TableColumn<ProductOrder, String> productNameCol = new TableColumn<>("Tên Sản Phẩm");
            TableColumn<ProductOrder, Integer> quantityCol = new TableColumn<>("Số lượng");
            TableColumn<ProductOrder, BigDecimal> amountCol = new TableColumn<>("Thành Tiền");

            productNameCol.setCellValueFactory(new PropertyValueFactory<>("productName"));
            quantityCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amountPrice"));

            productTable.getColumns().addAll(productNameCol, quantityCol, amountCol);
            productTable.setMinHeight(300);
            ScrollPane scrollPane = new ScrollPane(productTable);
            scrollPane.setFitToWidth(true);
            scrollPane.setPrefHeight(300);
            scrollPane.setPrefWidth(400);

            productVbox.getChildren().clear();
            productVbox.getChildren().add(scrollPane);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void showOrders() {
        String query = "SELECT distinct o.orderID, i.invoiceID, o.orderDate, i.paymentDate, i.paymentMethod, i.total AS totalPrice, o.status " +
                "FROM `Order` o " +
                "JOIN Cart_Order co ON o.orderID = co.orderID " +
                "JOIN Cart c ON co.cartID = c.cartID " +
                "JOIN Invoices i ON o.orderID = i.orderID " +
                "ORDER BY i.paymentDate DESC;";

        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                VBox vBox = createOrderVBox(
                        resultSet.getInt("invoiceID"),
                        resultSet.getInt("orderID"),
                        resultSet.getTimestamp("orderDate"),
                        resultSet.getTimestamp("paymentDate"),
                        resultSet.getString("paymentMethod"),
                        resultSet.getBigDecimal("totalPrice"),
                        resultSet.getBoolean("status")
                );
                invoicesAdmin.getChildren().add(vBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
        }
    }


    private VBox createOrderVBox(int invoiceID, int orderID, Timestamp orderDate, Timestamp paymentDate,
                                 String paymentMethod, BigDecimal totalPrice, boolean status) {

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
        productVBox.setPrefHeight(200);
        productVBox.setPrefWidth(400);
        productButton.setOnAction(event -> {
            if (!productVBox.isVisible()) {
                productVBox.setVisible(true);
                showProductFromOrder(orderID, productVBox);
                productButton.setText("Ẩn sản phẩm");
            } else {
                productVBox.setVisible(false);
                productVBox.getChildren().clear();
                productButton.setText("Hiển thị sản phẩm");
            }
        });
        Button aprovalButton;
        if (!status) {
            aprovalButton = new Button("Duyệt đơn");
            aprovalButton.setOnAction(event -> {
                updateStatusOrder(orderID);
                System.out.println("Duyệt đơn hàng " + orderID);
                vBox.getChildren().remove(aprovalButton);
            });
        } else {
            aprovalButton = null;
        }

        vBox.getChildren().addAll(invoiceIDLabel, orderIDLabel, orderDateLabel, paymentDateLabel, paymentMethodLabel, totalPriceLabel, productButton, productVBox , aprovalButton);

        return vBox;
    }
    private void updateStatusOrder(int orderID) {
        String query = "UPDATE `Order` SET status = ? WHERE orderID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setBoolean(1,true);
            ps.setInt(2,orderID);
            ps.executeUpdate();
            System.out.println("Cập nhật trạng thái thành công");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
