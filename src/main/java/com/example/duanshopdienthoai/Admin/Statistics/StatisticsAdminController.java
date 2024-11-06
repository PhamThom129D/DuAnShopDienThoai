package com.example.duanshopdienthoai.Admin.Statistics;

import com.example.duanshopdienthoai.DatabaseConnection;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class StatisticsAdminController {
    @FXML
    private TableView<TopProduct> topProduct;
    @FXML
    private TableColumn<TopProduct, Integer> rank;
    @FXML
    private TableColumn<TopProduct, String> productName;
    @FXML
    private TableColumn<TopProduct, Integer> quantity;

    public void initialize() throws SQLException {
        getTopProduct();
    }
    public void getTopProduct() throws SQLException {
        String queryTop5Product = "SELECT p.*, SUM(co.quantity) AS totalSold\n" +
                "FROM Products p\n" +
                "JOIN Cart c ON p.productID = c.productID\n" +
                "JOIN Cart_Order co ON c.cartID = co.cartID\n" +
                "JOIN `Order` o ON co.orderID = o.orderID\n" +
                "WHERE o.status = 'Đã thanh toán' \n" +
                "GROUP BY p.productID\n" +
                "ORDER BY totalSold DESC\n" +
                "LIMIT 5;\n";
        Connection conn = DatabaseConnection.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(queryTop5Product);
        while (rs.next()) {
            String productName = rs.getString("productName");
            int totalSold = rs.getInt("totalSold");
            TopProduct product = new TopProduct(productName, totalSold);
            topProduct.getItems().add(product);
        }
        setColumn();
    }

    private void setColumn() {
        rank.setCellValueFactory(cellData -> new SimpleIntegerProperty(topProduct.getItems().indexOf(cellData.getValue()) + 1).asObject());
        productName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        quantity.setCellValueFactory(new PropertyValueFactory<>("totalSold"));
    }
}
