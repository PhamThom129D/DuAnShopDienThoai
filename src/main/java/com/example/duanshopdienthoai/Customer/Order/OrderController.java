package com.example.duanshopdienthoai.Customer.Order;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import static com.example.duanshopdienthoai.ReUse.showConfirmation;


public class OrderController {
    @FXML
    private Label usernameOrder;
    @FXML
    private Label addressOrder;
    @FXML
    private Label phonenumberOrder;
    @FXML
    private Label dateOrder;
    @FXML
    private TableView<Order> productInfo;
    @FXML
    private TableColumn<Order, String> nameProduct;
    @FXML
    private TableColumn<Order, BigDecimal> priceProduct;
    @FXML
    private TableColumn<Order, Integer> quantityProduct;
    @FXML
    private TableColumn<Order, BigDecimal> amountProduct;
    @FXML
    private ChoiceBox paymentType;
    @FXML
    private Label totalPrice;
    private ObservableList<Order> orderList = FXCollections.observableArrayList();


    public void initialize() {
        showInfoCustomer();
        int orderID = getOrderIDFromUser(LoggedInUser.getInstance().getUserID());
        showProductOrder(orderID);

        paymentType.getItems().addAll("Tiền mặt", "Chuyển khoản");
        paymentType.getSelectionModel().selectFirst();

        paymentType.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            String selectPayment = paymentType.getValue().toString();
        });
    }

    private void showInfoCustomer() {
        String query = "Select u.username,u.address,u.phonenumber,o.orderDate from User u join `Order` o on u.userID=o.userID where u.userID=?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, LoggedInUser.getInstance().getUserID());
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String username = rs.getString("username");
                String address = rs.getString("address");
                String phonenumber = rs.getString("phonenumber");
                Timestamp timestamp = rs.getTimestamp("orderDate");
                String date = timestamp != null ? timestamp.toString() : "";

                usernameOrder.setText(username);
                addressOrder.setText(address);
                phonenumberOrder.setText(phonenumber);
                dateOrder.setText(date);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private int getOrderIDFromUser(int userID){
        String query = "Select orderID from `Order` where userID=? ORDER BY orderDate DESC LIMIT 1";
        try(Connection connection = DatabaseConnection.getConnection()){
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setInt(1, userID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("orderID");
                } else {
                    throw new SQLException("Không tìm thấy đơn hàng cho userID: " + userID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Lỗi khi lấy orderID cho userID: " + userID, e);
        }
    }

    private void showProductOrder(int orderID) {
        String query = "SELECT p.productName, p.price, co.quantity " +
                "FROM Cart_Order co " +
                "JOIN cart c ON co.cartID = c.cartID " +
                "JOIN products p ON c.productID = p.productID " +
                "WHERE co.orderID = ?";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setInt(1, orderID);
            ResultSet rs = ps.executeQuery();
            while(rs.next()){
                String productName = rs.getString("productName");
                BigDecimal price = rs.getBigDecimal("price");
                int quantity = rs.getInt("quantity");

                Order order = new Order(productName, price, quantity);
                orderList.add(order);
            }
            productInfo.setItems(orderList);
            setTableColumn();
            calculateTotalPrice();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void setTableColumn() {
        nameProduct.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceProduct.setCellValueFactory(new PropertyValueFactory<>("price"));
        priceProduct.setCellFactory(column -> new TableCell<>() {
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
        quantityProduct.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        amountProduct.setCellValueFactory(cellData -> {
            Order order = cellData.getValue();
            BigDecimal amount = order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity()));
            return new ReadOnlyObjectWrapper<>(amount);
        });
        amountProduct.setCellFactory(column -> new TableCell<>() {
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
    }

    private void calculateTotalPrice() {
        BigDecimal total = orderList.stream()
                .map(Order :: getAmount)
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        totalPrice.setText(total + " vnđ");
    }


    public void confirmOrder(ActionEvent event) throws IOException {
        if(showConfirmation("Bạn chắc chắn đặt đơn hàng này ?")){
            saveInvoices();
        Main.changeScene("Customer/HomeCustomer.fxml");
        }
        else {
            Main.changeScene("Customer/Cart.fxml");
        }

    }

    private void saveInvoices() {
        String query = "INSERT INTO Invoices(paymentMethod, orderID, paymentDate, total) VALUES (?,?,?,?)";
        try(Connection conn = DatabaseConnection.getConnection()){
            PreparedStatement preparedStatement = conn.prepareStatement(query);
            preparedStatement.setString(1, paymentType.getValue().toString());
            preparedStatement.setInt(2, getOrderIDFromUser(LoggedInUser.getInstance().getUserID()));
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = now.format(formatter);
            preparedStatement.setString(3, formattedDate);
            BigDecimal total = orderList.stream()
                    .map(order -> order.getPrice().multiply(BigDecimal.valueOf(order.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            preparedStatement.setBigDecimal(4, total);
            preparedStatement.executeUpdate();
            System.out.println("Thêm hóa đơn thành công");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void goback(ActionEvent event) throws IOException {
        Main.changeScene("Customer/HomeCustomer.fxml");
    }
}
