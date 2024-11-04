package com.example.duanshopdienthoai.Admin.Customer;

import com.example.duanshopdienthoai.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static com.example.duanshopdienthoai.ReUse.showError;

public class UpdateCustomerAdminController {
    @FXML
    private TextField customerNameField;
    @FXML
    private TextField customerPasswordField;
    @FXML
    private TextField customerAddressField;
    @FXML
    private TextField customerPhoneField;
    @FXML
    private RadioButton activeRadioButton;
    @FXML
    private RadioButton blockedRadioButton;

    private Customer customer;

    @FXML
    private void initialize() {
        ToggleGroup stateGroup = new ToggleGroup();
        activeRadioButton.setToggleGroup(stateGroup);
        blockedRadioButton.setToggleGroup(stateGroup);
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        System.out.println("Tên: " + customer.getCustomerName());
        customerNameField.setText(customer.getCustomerName());
        customerPasswordField.setText(customer.getCustomerPassword());
        customerAddressField.setText(customer.getCustomerAddress());
        customerPhoneField.setText(customer.getCustomerPhone());

        if (customer.getCustomerState()) {
            activeRadioButton.setSelected(true);
        } else {
            blockedRadioButton.setSelected(true);
        }
    }

    @FXML
    public void updateCustomer() {
        if (customer == null) {
            showError("Error", "No customer data available to update.");
            return;
        }

        if (customerNameField.getText().isEmpty() || customerPasswordField.getText().isEmpty() ||
                customerAddressField.getText().isEmpty() || customerPhoneField.getText().isEmpty()) {
            showError("Warning", "All fields must be filled.");
            return;
        }

        customer.setCustomerName(customerNameField.getText());
        customer.setCustomerPassword(customerPasswordField.getText());
        customer.setCustomerAddress(customerAddressField.getText());
        customer.setCustomerPhone(customerPhoneField.getText());
        customer.setCustomerState(activeRadioButton.isSelected());

        String query = "UPDATE User SET username = ?, password = ?, address = ?, phoneNumber = ?, state = ? WHERE userID = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, customer.getCustomerName());
            preparedStatement.setString(2, customer.getCustomerPassword());
            preparedStatement.setString(3, customer.getCustomerAddress());
            preparedStatement.setString(4, customer.getCustomerPhone());
            preparedStatement.setBoolean(5, customer.getCustomerState());
            preparedStatement.setInt(6, customer.getCustomerID());
            preparedStatement.executeUpdate();

            Stage stage = (Stage) customerNameField.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showError("Error", "Could not update customer information: " + e.getMessage());
        }
    }

}