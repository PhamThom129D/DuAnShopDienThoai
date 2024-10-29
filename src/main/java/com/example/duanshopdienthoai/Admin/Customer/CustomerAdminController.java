package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.ReUse;
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
import java.sql.*;

import static com.example.duanshopdienthoai.ReUse.showConfirmation;

public class CustomerAdminController {
    @FXML
    private TableView<Customer> customerTableView;
    @FXML
    private TableColumn<Customer, Integer> customerID;
    @FXML
    private TableColumn<Customer, String> customerPassword;
    @FXML
    private TableColumn<Customer, String> customerName;
    @FXML
    private TableColumn<Customer, String> customerAddress;
    @FXML
    private TableColumn<Customer, String> customerPhone;
    @FXML
    private TableColumn<Customer, String> customerState;
    @FXML
    private TableColumn<Customer, Void> actionCustomer;

    public void initialize() {
        showCustomerTableView();
        customerTableView.setEditable(true);
        reLoad();
    }
    private void showCustomerTableView() {
        String sql = "SELECT userID,state,username,password,address,phoneNumber FROM user where role ='Customer' ";
        try(Connection connection = DatabaseConnection.getConnection();
      Statement statement = connection.createStatement()){
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int userID = resultSet.getInt("userID");
                String username = resultSet.getString("username");
                String password = resultSet.getString("password");
                String address = resultSet.getString("address");
                String phone = resultSet.getString("phoneNumber");
                Boolean state = resultSet.getBoolean("state");

                Customer customer = new Customer(userID,username,password,address,phone,state);
                customerTableView.getItems().add(customer);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        setCustomerTableView();
    }

    private void setCustomerTableView() {
        customerID.setCellValueFactory(new PropertyValueFactory<>("customerID"));
        customerName.setCellValueFactory(new PropertyValueFactory<>("customerName"));
        customerPassword.setCellValueFactory(new PropertyValueFactory<>("customerPassword"));
        customerAddress.setCellValueFactory(new PropertyValueFactory<>("customerAddress"));
        customerState.setCellValueFactory(new PropertyValueFactory<>("customerState"));
        customerPhone.setCellValueFactory(new PropertyValueFactory<>("customerPhone"));

        actionCustomer.setCellFactory(col -> new TableCell<Customer, Void>() {
            private final Button blockButton = new Button("Khóa tài khoản");
            private final Button updateButton = new Button("Sửa thông tin");

            {
                blockButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    if (showConfirmation("Bạn có chắc chắn muốn khóa tài khoản " + customer.getCustomerName() + "?")) {
                        try {
                            blockCustomer(customer.getCustomerID());
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    reLoad();
                });

                updateButton.setOnAction(e -> {
                    Customer customer = getTableView().getItems().get(getIndex());
                    if (showConfirmation("Bạn muốn cập nhật tài khoản này ?")){
                        updateCustomer(customer);
                    }
                        reLoad();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(new HBox(5, blockButton, updateButton));
                }
            }
        });
    }

    private void updateCustomer(Customer customer) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("UpdateCustomer_Admin.fxml"));
            Parent root = loader.load();
            UpdateCustomerAdminController controller = loader.getController();
            controller.setCustomer(customer);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

        public void blockCustomer(int customerID) throws SQLException {
        String query = "UPDATE User SET state = false WHERE userID = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, customerID);
            ps.executeUpdate();
            System.out.println("Blocked Customer ID: " + customerID);
        }
    }
    public void reLoad(){
        customerTableView.getItems().clear();
        showCustomerTableView();
    }
}
