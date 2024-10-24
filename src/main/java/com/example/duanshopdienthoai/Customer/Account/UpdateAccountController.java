package com.example.duanshopdienthoai.Customer.Account;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateAccountController {
    @FXML
    private TextField usernameField;
    @FXML
    private TextField phonenumberField;
    @FXML
    private TextField addressField;


    public void handleEdit(String username , String phonenumber , String address) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String query = "Update user set username = ?,phoneNumber=?,address=? where userID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, phonenumber);
        preparedStatement.setString(3, address);
        preparedStatement.setInt(4, LoggedInUser.getInstance().getUserID());
        preparedStatement.executeUpdate();
    }

    public void handleSave(ActionEvent event) throws SQLException, IOException {
        String username = usernameField.getText();
        String phonenumber = phonenumberField.getText();
        String address = addressField.getText();
        if (username.equals("") || phonenumber.equals("") || address.equals("")) {
            showAlert("Không được để trống");
        }else {
            handleEdit(username,phonenumber,address);
            showAlert("Cập nhật thành công");
            goback(event);
        }
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.show();
    }

    public void goback(ActionEvent event) throws IOException {
        Main.changeScene("AccountCustomer.fxml");
    }
}
