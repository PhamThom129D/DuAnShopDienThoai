package com.example.duanshopdienthoai.Customer.Account;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccountCustomerController {
    @FXML
    Label usernameAccount;
    @FXML
    Label phonenumberAccount;
    @FXML
    Label addressAccount;
    @FXML
    private Label stateAccount;
    @FXML
    public void initialize() throws SQLException {
        loadUser();
    }

    public void loadUser() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user WHERE userID = ?";
        try(PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, LoggedInUser.getInstance().getUserID());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                usernameAccount.setText(resultSet.getString("username"));
                phonenumberAccount.setText(resultSet.getString("phoneNumber"));
                addressAccount.setText(resultSet.getString("address"));
                Boolean state = resultSet.getBoolean("state");
                if (state) {
                    stateAccount.setText("Đang hoạt động");
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void goback() throws IOException {
        Main.changeScene("Customer/HomeCustomer.fxml");
    }

    public void handleEdit() throws IOException {
        Main.changeScene("Customer/UpdateAccount.fxml");
    }
}
