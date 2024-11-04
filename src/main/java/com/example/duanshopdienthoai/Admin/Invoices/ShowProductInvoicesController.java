package com.example.duanshopdienthoai.Admin.Invoices;

import com.example.duanshopdienthoai.Customer.Invoice.ProductOrder;
import com.example.duanshopdienthoai.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ShowProductInvoicesController {
    @FXML
    private TableView<ProductInvoicesItem> productInvoices;
    @FXML
    private TableColumn<ProductInvoicesItem , Integer> stt;
    @FXML
    private TableColumn<ProductInvoicesItem, String> productName;
    @FXML
    private TableColumn<ProductInvoicesItem, BigDecimal> productPrice;
    @FXML
    private TableColumn<ProductInvoicesItem, Integer> productQuantity;
    @FXML
    private TableColumn<ProductInvoicesItem, String> productAmount;

    private InvoiceItem invoiceItem;

    public void initialize() {
    }

    public void setInvoices(InvoiceItem invoiceItem) {
        this.invoiceItem = invoiceItem;
        showProductFromOrder(invoiceItem.getOrderID());
    }
    private void showProductFromOrder(int orderID) {
        String query = "SELECT o.status, p.productName,p.price, co.quantity, p.price * co.quantity AS amountPrice " +
                "FROM `Order` o " +
                "JOIN Cart_Order co ON o.orderID = co.orderID " +
                "JOIN Cart c ON co.cartID = c.cartID " +
                "JOIN Products p ON c.productID = p.productID " +
                "WHERE o.orderID = ?";
        try (Connection connection = DatabaseConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                BigDecimal price = resultSet.getBigDecimal("price");
                BigDecimal amountPrice = resultSet.getBigDecimal("amountPrice");

                ProductInvoicesItem productInvoicesItem = new ProductInvoicesItem(productName,quantity,price,amountPrice);
                productInvoices.getItems().add(productInvoicesItem);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setTableColumn();
    }
    private void setTableColumn(){
        stt.setCellValueFactory(cellData -> new SimpleIntegerProperty(productInvoices.getItems().indexOf(cellData.getValue()) +1).asObject());
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        productQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        productAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));

    }
}
