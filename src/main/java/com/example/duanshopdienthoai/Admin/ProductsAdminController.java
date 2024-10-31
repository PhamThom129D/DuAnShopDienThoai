
package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private TableColumn<Product, ImageView> ImageProducts;
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
                String urlImage = resultSet.getString("productImage");
                String productName = resultSet.getString("productName");
                int quantity = resultSet.getInt("quantity");
                BigDecimal price = resultSet.getBigDecimal("price");
                String description = resultSet.getString("description");
                Boolean stock = resultSet.getBoolean("stock");
                String type = resultSet.getString("type");

                ImageView productImage = new ImageView(urlImage);
                productImage.setFitHeight(80);
                productImage.setFitWidth(70);

                Product product = new Product(productID,productImage,productName,quantity,price,type,description,stock);
                productTableView.getItems().add(product);
                System.out.println(productID);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        setProductTableView();
    }
    public void setProductTableView() {
        IDProducts.setCellValueFactory(new PropertyValueFactory<>("idProduct"));
        ImageProducts.setCellValueFactory(new PropertyValueFactory<>("imageProduct"));
        NameProducts.setCellValueFactory(new PropertyValueFactory<>("nameProduct"));
        PriceProducts.setCellValueFactory(new PropertyValueFactory<>("priceProduct"));
        PriceProducts.setCellFactory(column -> new TableCell<>() {
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
        QuantityProducts.setCellValueFactory(new PropertyValueFactory<>("quantityProduct"));
        TypeProducts.setCellValueFactory(new PropertyValueFactory<>("typeProduct"));
        DescriptionProducts.setCellValueFactory(new PropertyValueFactory<>("descriptionProduct"));
        DescriptionProducts.setCellFactory(column -> new TableCell<Product, String>() {
            private final TextArea textArea = new TextArea();
            {
                textArea.setWrapText(true);
                textArea.setEditable(false);
                textArea.setPrefSize(200,50);
                textArea.setStyle("-fx-border-color: transparent; -fx-background-color: transparent;");
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                }else {
                    textArea.setText(item);
                    setGraphic(textArea);
                }
            }
        });
        StateProducts.setCellValueFactory(new PropertyValueFactory<>("stateProduct"));
        StateProducts.setCellFactory(col -> new TableCell<Product, Boolean>() {
            private final ImageView imageView = new ImageView();
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                }else {
                    if(item){
                        String urlImage = getClass().getResource("/com/example/duanshopdienthoai/Image/check.jpg").toExternalForm();
                        imageView.setImage(new Image(urlImage));

                    }else {
                        String urlImage2 = getClass().getResource("/com/example/duanshopdienthoai/Image/uncheck.jpg").toExternalForm();
                        imageView.setImage(new Image(urlImage2));
                    }
                    imageView.setFitHeight(20);
                    imageView.setFitWidth(20);

                }
                setGraphic(imageView);
            }
        });
        ActionProducts.setCellFactory(col -> new TableCell<Product, Void>() {
            private final Button stockButton = new Button("Hết Hàng");
            private final Button updateButton = new Button("Sửa thông tin");

            {
                stockButton.setOnAction((ActionEvent event) -> {
                    Product product = getTableView().getItems().get(getIndex());
                    if(ReUse.showConfirmation("Sản phẩm này đã hết hàng ?")) {
                        stockProduct(product.getIdProduct());
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
                    Product product = getTableView().getItems().get(getIndex());

                    if(!product.getStateProduct()){
                        setGraphic(updateButton);
                    }else {
                        setGraphic(new HBox(5,stockButton,updateButton));
                    }
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
        reLoad();
    }
    public void reLoad(){
        productTableView.getItems().clear();
        showProductTableView();
    }
}
