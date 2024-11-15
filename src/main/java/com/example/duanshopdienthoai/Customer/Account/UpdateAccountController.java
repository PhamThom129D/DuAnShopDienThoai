package com.example.duanshopdienthoai.Customer.Account;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static com.example.duanshopdienthoai.ReUse.showAlert;

public class UpdateAccountController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField passwordField;
    @FXML
    private TextField phonenumberField;
    @FXML
    private TextField addressField;

    @FXML
    public void initialize() throws SQLException {
        setInfoAccount();
    }
    public void setInfoAccount() throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT * FROM user WHERE userID = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        preparedStatement.setInt(1, LoggedInUser.getInstance().getUserID());
        ResultSet rs = preparedStatement.executeQuery();
        while(rs.next()) {
            usernameField.setText(rs.getString("username"));
            passwordField.setText(rs.getString("password"));
            phonenumberField.setText(rs.getString("phonenumber"));
            addressField.setText(rs.getString("address"));
        }
        preparedStatement.executeQuery();
    }

    public void handleSave(String username,String password , String phonenumber , String address) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "Update user set username = ?,password=?,phoneNumber=?,address=? where userID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, password);
        preparedStatement.setString(3, phonenumber);
        preparedStatement.setString(4, address);
        preparedStatement.setInt(5, LoggedInUser.getInstance().getUserID());
        preparedStatement.executeUpdate();
    }

    public void handleEdit(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phonenumber = phonenumberField.getText();
        String address = addressField.getText();
        if (username.equals("") || phonenumber.equals("") || address.equals("")) {
            showAlert("Không được để trống");
        }else {
            handleSave(username,password,phonenumber,address);
            showAlert("Cập nhật thành công");
            goback(event);
        }
    }

    public void goback(ActionEvent event) throws IOException {
        Main.changeScene("Customer/AccountCustomer.fxml");
    }
}
