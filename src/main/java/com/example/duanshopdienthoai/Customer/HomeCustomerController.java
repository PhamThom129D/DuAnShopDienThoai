package com.example.duanshopdienthoai.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import com.example.duanshopdienthoai.ReUse;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.duanshopdienthoai.ReUse.showConfirmation;

public class HomeCustomerController {
    @FXML
    private FlowPane topProduct;
    @FXML
    private FlowPane randomProduct;
    @FXML
    private FlowPane showProduct;
    @FXML
    private TextField searchTextFile;
    @FXML
    private Label countCart;
    private Connection conn;
    @FXML
    public void initialize() throws SQLException {
        loadProducts();
    }

    public HomeCustomerController() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        conn = connection;
    }

    private void loadProducts() throws SQLException {
        String queryTop5Product = "SELECT p.*, SUM(co.quantity) AS totalSold\n" +
                "FROM Products p\n" +
                "JOIN Cart c ON p.productID = c.productID\n" +
                "JOIN Cart_Order co ON c.cartID = co.cartID\n" +
                "JOIN `Order` o ON co.orderID = o.orderID\n" +
                "WHERE o.status = 'Đã thanh toán' and p.quantity > 0 \n" +
                "GROUP BY p.productID\n" +
                "ORDER BY totalSold DESC\n" +
                "LIMIT 5;\n";
        String queryRandomProduct = "SELECT * FROM products ORDER BY RAND() LIMIT 15";

        try (PreparedStatement pstmtTop = conn.prepareStatement(queryTop5Product);
             PreparedStatement pstmtRandom = conn.prepareStatement(queryRandomProduct);
             ResultSet rs1 = pstmtTop.executeQuery();
             ResultSet rs2 = pstmtRandom.executeQuery()) {

            loadProductFromDatabase(topProduct, rs1);
            loadProductFromDatabase(randomProduct, rs2);
            setCountCart();
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
            String productName = rs.getString("productName");
            BigDecimal price = rs.getBigDecimal("price");
            int quantity = rs.getInt("quantity");
            String type = rs.getString("type");
            String description = rs.getString("description");

            VBox vbox = new VBox(10);
            vbox.setAlignment(Pos.CENTER);
            vbox.getStyleClass().add("top-product");

            ImageView imageView = new ImageView(imageUrl);
            imageView.setFitWidth(155);
            imageView.setFitHeight(145);

            Label nameLabel = new Label(productName);
            Label priceLabel = new Label( price +" vnđ");
            Label quantityLabel = new Label("Số lượng: " + quantity);

            if (stock && quantity > 0) {
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
            setCountCart();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String setCountCart() {
        String query = "SELECT COUNT(*) as SoLuongSanPham FROM Cart where userID = ? and checkbox = 0";
        String count = null;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, LoggedInUser.getInstance().getUserID());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getString("SoLuongSanPham");
            }
            countCart.setText(count);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Đếm số lượng sản phẩm lỗi");
        }
        return count;
    }
//      Thanh menu trên
    public void showHome(ActionEvent event) throws IOException, SQLException {
        Main.changeScene("Customer/HomeCustomer.fxml");
    }
    public void searchProduct(ActionEvent event) {
        String searchText = searchTextFile.getText().trim();
        if(searchText.isEmpty()){
            ReUse.showAlert("Vui lòng nhập từ khóa tìm kiếm");
            return;
        };
        showProduct.getChildren().clear();
        String querySearchProduct = "SELECT * FROM products WHERE productName LIKE ? or description LIKE ? or type LIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(querySearchProduct)) {
            pstmt.setString(1, "%" + searchText + "%");
            pstmt.setString(2, "%" + searchText + "%");
            pstmt.setString(3, "%" + searchText + "%");
            try (ResultSet rs3 = pstmt.executeQuery()) {
                if(!rs3.next()){
                    ReUse.showAlert("Không tìm thấy sản phẩm nào với từ khóa : " + searchText);
                    return;
                }
                loadProductFromDatabase(showProduct, rs3);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            ReUse.showAlert("Lỗi khi tìm kiếm sản phẩm");
        }
    }
    public void showInvoice() throws IOException {Main.changeScene("Customer/Invoice.fxml");
    }
    public void showAccount() throws IOException {
        Main.changeScene("Customer/AccountCustomer.fxml");
    }
    public void showCart() throws IOException {
        if(countCart.getText().equals("0")) {
            ReUse.showAlert("Không có sản phẩm nào trong giỏ hàng .");
        }else{
            Main.changeScene("Customer/Cart.fxml");
        }
    }
//      Thanh menu bên
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
    public void showSamSung() {
        showProductType("Samsung");
    }
    public void showXiaomi() {
        showProductType("Xiaomi");
    }
    public void showOppo() {
        showProductType("Oppo");
    }
    public void showNokia() {
        showProductType("Nokia");
    }
    public void goOut() throws IOException {
        if(showConfirmation("Bạn muốn đăng xuất khỏi hệ thống?")) {
            LoggedInUser.logout();
            Main.changeScene("Login.fxml");
        }
    }


}
