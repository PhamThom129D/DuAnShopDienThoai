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

public class SignController{
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
    private static final String ADDRESS_REGEX = "^(?=.*[A-Za-zÀ-ỹ])[A-Za-zÀ-ỹ0-9\\s]+(?:\\s[A-Za-zÀ-ỹ\\s]+)*$";


    public void signUp(ActionEvent actionEvent) throws SQLException {
        String username = this.username.getText();
        String password = this.password.getText();
        String rePassword = this.rePassword.getText();
        String address = this.address.getText();
        String phoneNumber = this.phoneNumber.getText();

        if(checkAccount(username)){
            showAlert("Tên đăng nhập đã tồn tại.");
        }else if(username.length() <6 ){
            showAlert("Tên đăng nhập quá ngắn . Tên đăng nhập tối thiểu gồm 6 kí tự.");
        }else if(password.length() <6 ){
            showAlert("Mật khẩu quá ngắn . Mật khẩu tối thiểu 8 kí tự.");
        }else if(!isValidAddress(address)){
            showAlert("Địa chỉ không hợp lệ.");
        }else if(!isValidPhoneNumber(phoneNumber)) {
            showAlert("Số điện thoại không hợp tệ . Số điện thoại phải gồm 10 chữ số và bắt đầu bằng số 0.");
        }else if(!rePassword.equals(password) ){
            showAlert("Mật khẩu không trùng khớp.");
        }else if(username.isEmpty()||password.isEmpty()||rePassword.isEmpty()||address.isEmpty()||phoneNumber.isEmpty()){
            showAlert("Không được để trống.");
        }else {
            saveUserNew(username,password,address,phoneNumber);
            showAlert("Đăng ký thành công. Mời đăng nhập lại.");
            showLogin();
        }
    }
    private void saveUserNew(String username , String password, String address, String phoneNumber) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "INSERT INTO User(username, password, address, phoneNumber)" + "VALUES(?,?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, address);
            ps.setString(4, phoneNumber);
            System.out.println("Khách hàng mới : " + username + " " + password + " " + address + " " + phoneNumber);
            ps.executeUpdate();
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private boolean checkAccount(String username) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "SELECT username FROM User WHERE username = ?";
        try(PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        }catch (SQLException e) {
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
