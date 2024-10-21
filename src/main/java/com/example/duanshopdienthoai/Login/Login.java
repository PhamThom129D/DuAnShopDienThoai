package com.example.duanshopdienthoai.Login;

import com.example.duanshopdienthoai.DatabaseConnection;
import com.example.duanshopdienthoai.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;

    public void initialize() {
    }


    public void Login(ActionEvent actionEvent) throws SQLException, IOException {
        String username = this.username.getText();
        String password = this.password.getText();
        String role = checkUser(username, password);
        if(role != null){
            System.out.println("Đăng nhập thành công");
            switch(role) {
                case "Admin":
                    showAlert("Hello Admin " + username );
                    Main.changeScene("HomeAdmin.fxml");
                    break;
                case "User":
                    showAlert("Hello User " + username );
                    Main.changeScene("HomeUser.fxml");
                    break;
                case "Customer":
                    showAlert("Welcom " + username + " to join us!" );
                    Main.changeScene("HomeCustomer.fxml");
                    break;

            }
        }else{
            System.out.println("Đăng nhập thất bại");
            showAlert("Login Fail");
            this.username.requestFocus();
        }
    }

    private String checkUser(String username, String password) throws SQLException {
        Connection conn = DatabaseConnection.getConnection();
        String query = "select * from user where username=? and password=?";
        try(PreparedStatement ps = conn.prepareStatement(query)){
            ps.setString(1,username);
            ps.setString(2,password);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                boolean state = rs.getBoolean("state");
                if(!state){
                    showAlert("Account Block!");
                    return null;
                }
                return rs.getString("role");
            } else {return null;}
        }catch (SQLException e){
            e.printStackTrace();
            System.out.println("Lỗi kết nối database");
            return null;
        }
    }

    public void showSignUp(ActionEvent actionEvent) throws IOException {
        Main.changeScene("Sign.fxml");
    }

    public void forgetPassword(ActionEvent actionEvent) {
        showAlert("Wait for verification email");
    }
    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.show();
    }
}
