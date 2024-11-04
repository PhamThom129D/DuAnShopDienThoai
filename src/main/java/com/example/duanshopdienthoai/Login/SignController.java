package com.example.duanshopdienthoai.Login;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static com.example.duanshopdienthoai.ReUse.showAlert;

public class SignController {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;
    @FXML
    private PasswordField rePassword;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField address;

    private static final String PHONE_REGEX = "^0\\d{9}$";
    private static final String ADDRESS_REGEX = "^(?=.*[A-Za-zÀ-ỹ])[A-Za-zÀ-ỹ0-9\\s]+(?:\\s[A-Za-zÀ-ỹ\\s]+)*$|^(?!\\s*$).*$";

    public void signUp(ActionEvent actionEvent) throws SQLException {
        String usernameString = this.username.getText();
        String passwordString = this.password.getText();
        String rePasswordString = this.rePassword.getText();
        String addressString = this.address.getText();
        String phoneNumberString = this.phoneNumber.getText();

        if (checkUserName(usernameString) && checkPassword(passwordString) &&
                checkRePassword(passwordString, rePasswordString) && checkAddress(addressString) &&
                checkPhone(phoneNumberString)) {
            saveUserNew(usernameString, passwordString, addressString, phoneNumberString);
            showAlert("Đăng ký thành công \n Xin vui lòng đăng nhập lại");
            showLogin();
        }
    }


    public boolean checkUserName(String username) throws SQLException {
        if (checkAccount(username)) {
            setColorLabel(this.username);
            showAlert("Tên đăng nhập đã tồn tại.");
            return false;
        } else if (username.length() < 6 || username.isEmpty()) {
            setColorLabel(this.username);
            showAlert("Tên đăng nhập không được để trống \n Tối thiểu 6 ký tự");
            return false;
        } else {
            resetColorLabel(this.username);
            return true;
        }
    }

    public boolean checkPassword(String password) {
        if (password.length() < 6 || password.isEmpty()) {
            setColorLabel(this.password);
            showAlert("Mật khẩu không được để trống \n Tối thiểu 6 ký tự");
            return false;
        } else {
            resetColorLabel(this.password);
            return true;
        }
    }

    public boolean checkRePassword(String password, String rePassword) {
        if (!rePassword.equals(password) || rePassword.isEmpty()) {
            setColorLabel(this.rePassword);
            showAlert("Nhập lại mật khẩu không được để trống \n Mật khẩu nhập lại phải trùng với mật khẩu vừa đăng ký");
            return false;
        } else {
            resetColorLabel(this.rePassword);
            return true;
        }
    }

    public boolean checkPhone(String phoneNumber) {
        if (!isValidPhoneNumber(phoneNumber)) {
            setColorLabel(this.phoneNumber);
            showAlert("Số điện thoại không hợp lệ \n Gồm 10 chữ số và bắt đầu bằng số 0.");
            return false;
        } else {
            resetColorLabel(this.phoneNumber);
            return true;
        }
    }

    public boolean checkAddress(String address) {
        if (!isValidAddress(address)) {
            setColorLabel(this.address);
            showAlert("Địa chỉ không hợp lệ \n Phải chứa ít nhất một ký tự chữ.");
            return false;
        } else {
            resetColorLabel(this.address);
            return true;
        }
    }


    private void saveUserNew(String username, String password, String address, String phoneNumber) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO User(username, password, address, phoneNumber)" + "VALUES(?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, address);
            ps.setString(4, phoneNumber);
            System.out.println("Khách hàng mới : " + username + " " + password + " " + address + " " + phoneNumber);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setColorLabel(TextField textField) {
        textField.setStyle("-fx-text-fill: rgba(255,0,0); -fx-border-color: rgba(255,0,0);");

    }

    private void resetColorLabel(TextField textField) {
        textField.setStyle("");
    }

    private boolean checkAccount(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT LOWER(username) FROM User WHERE LOWER(username) = LOWER(?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showLogin() {
        try {
            Main.changeScene("Login.fxml");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        return phoneNumber.matches(PHONE_REGEX);
    }

    private boolean isValidAddress(String address) {
        return address.matches(ADDRESS_REGEX);
    }
}