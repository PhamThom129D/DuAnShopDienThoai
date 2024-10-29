package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;

public class ProductsAdminController {
    @FXML
    private TableView<Product> productTableView;
    @FXML
    private TableColumn<Product, Integer> IDProducts;
    @FXML
    private TableColumn<Product, String> ImageProducts;
    @FXML
    private TableColumn<Product, String> NameProducts;
    @FXML
    private TableColumn<Product, BigDecimal> PriceProducts;
    @FXML
    private TableColumn<Product, Integer> QuantityProducts;
    @FXML
    private TableColumn<Product, String> TypeProducts;
    @FXML
    private TableColumn<Product, String> DescriptionProducts;
    @FXML
    private TableColumn<Product, Boolean> StateProducts;
    @FXML
    private TableColumn<Product, Void> ActionProducts;

    public void initialize() {
        showProductTableView();
        productTableView.setEditable(true);
        reLoad();

    }
    private void showProductTableView(){
        String query = "SELECT productID,productImage,productName,quantity, price,description,stock,type FROM products";
        try(Connection connection = DatabaseConnection.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while(resultSet.next()){
                int productID = resultSet.getInt("productID");
                String productImage = resultSet.getString("productImage");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                BigDecimal price = resultSet.getBigDecimal("price");
                String description = resultSet.getString("description");
                Boolean stock = resultSet.getBoolean("stock");
                String type = resultSet.getString("type");

                Product product = new Product(productID,productImage,productName,quantity,price,type,description,stock);
                productTableView.getItems().add(product);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setProductTableView();
    }
    public void setProductTableView() {
        IDProducts.setCellValueFactory(new PropertyValueFactory<>("IDProducts"));
        ImageProducts.setCellValueFactory(new PropertyValueFactory<>("ImageProducts"));
        NameProducts.setCellValueFactory(new PropertyValueFactory<>("NameProducts"));
        PriceProducts.setCellValueFactory(new PropertyValueFactory<>("PriceProducts"));
        QuantityProducts.setCellValueFactory(new PropertyValueFactory<>("QuantityProducts"));
        TypeProducts.setCellValueFactory(new PropertyValueFactory<>("TypeProducts"));
        DescriptionProducts.setCellValueFactory(new PropertyValueFactory<>("DescriptionProducts"));
        StateProducts.setCellValueFactory(new PropertyValueFactory<>("StateProducts"));
        ActionProducts.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button stockButton = new Button("Hết Hàng");
            private final Button updateButton = new Button("Sửa thông tin");

            {
                stockButton.setOnAction((ActionEvent event) -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if(ReUse.showConfirmation("Sản phẩm này đã hết hàng ?")) {
                        stockProduct(product.getIDProducts());
                    }
                    reLoad();
                });
                updateButton.setOnAction((ActionEvent event) -> {
                    Product product = getTableView().getItems().get(getIndex());
                    updateProduct(product);
                    reLoad();
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                }else {
                    setGraphic(new HBox(5,stockButton,updateButton));
                }
            }
        });
    }

    private void stockProduct(int idProducts) {
        String query = "Update products Set stock = false WHERE productID = ?";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1,idProducts);
            ps.executeUpdate();
            System.out.println("Sản phẩm hết hàng : " + idProducts);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void updateProduct(Product product) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateProducts_Admin.fxml"));
            Parent root = loader.load();
            UpdateProductsAdminController controllerProduct = loader.getController();
            controllerProduct.setProducts(product);
            Stage stage = new Stage();
            stage.setTitle("Cập nhật sản phẩm");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }


    public void addProduct(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AddProducts_Admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Thêm sản phẩm mới");
            stage.setScene(new Scene(root));
            stage.showAndWait();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void searchProductAdmin(ActionEvent event) {
    }
    public void reLoad(){
        productTableView.getItems().clear();
        showProductTableView();
    }
}
