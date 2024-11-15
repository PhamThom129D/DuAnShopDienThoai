package com.example.duanshopdienthoai.Admin.Invoices;

import com.example.duanshopdienthoai.Admin.Products.UpdateProductsAdminController;
import com.example.duanshopdienthoai.Customer.Invoice.ProductOrder;
import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class InvoiceAdminController {
    @FXML
    private TableView<InvoiceItem> invoicesAdmin;
    @FXML
            private TableColumn<InvoiceItem, Integer> invoiceID;
    @FXML
            private TableColumn<InvoiceItem, Integer> orderID; ;
            @FXML
                    private TableColumn<InvoiceItem, Timestamp> orderDate;
    @FXML
    private TableColumn<InvoiceItem, Timestamp> paymentDate;
    @FXML
    private TableColumn<InvoiceItem, String> paymentMethod;
    @FXML
    private TableColumn<InvoiceItem, String> status;
    @FXML
            private TableColumn<InvoiceItem, BigDecimal> total;
    @FXML
            private TableColumn<InvoiceItem, String> action;
    ObservableList<ProductOrder> productList = FXCollections.observableArrayList();
    public void initialize() {
        showOrders();
        invoicesAdmin.setEditable(true);
        reload();

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
                        int invoiceID = resultSet.getInt("invoiceID");
                        int orderID = resultSet.getInt("orderID");
                        Timestamp orderDate=resultSet.getTimestamp("orderDate");
                        Timestamp paymentDate=resultSet.getTimestamp("paymentDate");
                        String paymentMethod=resultSet.getString("paymentMethod");
                        BigDecimal totalPrice=resultSet.getBigDecimal("totalPrice");
                        String paid=resultSet.getString("status");

                        InvoiceItem invoiceItem = new InvoiceItem(invoiceID,orderID,orderDate,paymentDate,paymentMethod,totalPrice,paid);
                        invoicesAdmin.getItems().add(invoiceItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tải danh sách đơn hàng: " + e.getMessage());
        }
        setColumn();
    }

    private void setColumn(){
        invoiceID.setCellValueFactory(new PropertyValueFactory<>("invoiceID"));
        orderID.setCellValueFactory(new PropertyValueFactory<>("orderID"));
        orderDate.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        paymentDate.setCellValueFactory(new PropertyValueFactory<>("paymentDate"));
        paymentMethod.setCellValueFactory(new PropertyValueFactory<>("paymentMethod"));
        status.setCellValueFactory(new PropertyValueFactory<>("paid"));
        total.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        total.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + " vnđ");
                }
            }
        });
        action.setCellValueFactory(new PropertyValueFactory<>("paid"));
        action.setCellFactory(col -> new TableCell<InvoiceItem, String>() {
            private final Button showProductButton = new Button("Chi tiết hóa đơn");
            private final ChoiceBox<String> payStatus = new ChoiceBox<>();

            {
                showProductButton.setOnAction(e -> {
                    InvoiceItem invoiceItem = getTableView().getItems().get(getIndex());
                    showProduct(invoiceItem);
                });
                payStatus.getItems().addAll("Chưa thanh toán", "Đã thanh toán", "Hủy đơn");
                payStatus.setValue("Trạng thái đơn hàng");
                payStatus.setOnAction(e -> {
                    String selectedStatus = payStatus.getValue();
                    InvoiceItem invoiceItem = getTableView().getItems().get(getIndex());
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Xác nhận thay đổi trạng thái");
                    alert.setHeaderText("Bạn có chắc chắn muốn thay đổi trạng thái đơn hàng sang " + selectedStatus + "?");
                    alert.setContentText("Nhấn OK để xác nhận hoặc Cancel để hủy.");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            updateStatusOrder(invoiceItem.getOrderID(), selectedStatus);
                            payStatus.setValue(selectedStatus);
                            reload();
                        }
                    });
                });

            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView() == null) {
                    setGraphic(null);
                } else {
                    InvoiceItem invoiceItem = getTableView().getItems().get(getIndex());
                    String status = invoiceItem.getPaid();
                    if (("Chưa thanh toán").equals(status)) {
                        setGraphic(new HBox(payStatus, showProductButton));
                    } else {
                        setGraphic(showProductButton);
                    }
                }
            }

        });
        }
        private void showProduct(InvoiceItem invoiceItem){
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("showProductInvoice.fxml"));
            Parent root = loader.load();
            ShowProductInvoicesController controller = loader.getController();
            controller.setInvoices(invoiceItem);
            Stage stage = new Stage();
            stage.setTitle("Chi tiết hóa đơn");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        }
    private void updateStatusOrder(int orderID , String payStatus) {
        String query = "UPDATE `Order` SET status = ? WHERE orderID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1,payStatus);
            ps.setInt(2,orderID);
            ps.executeUpdate();
            System.out.println("Cập nhật trạng thái thành công");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void reload() {
        invoicesAdmin.getItems().clear();
        showOrders();
    }

}
