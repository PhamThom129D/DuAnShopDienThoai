package com.example.duanshopdienthoai.Admin;

import com.example.duanshopdienthoai.Login.LoggedInUser;
import com.example.duanshopdienthoai.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import java.io.IOException;

import static com.example.duanshopdienthoai.ReUse.showConfirmation;
import static com.example.duanshopdienthoai.ReUse.showError;

public class HomeAdminController {
    @FXML
    private AnchorPane contentAdmin;

    public void showCustomer() throws IOException {
        changeSceneAdmin("Customer_Admin.fxml");
    }

    public void showProducts() throws IOException {
        changeSceneAdmin("Products_Admin.fxml");
    }

    public void showUser() throws IOException {
        changeSceneAdmin("User_Admin.fxml");
    }

    public void showInvoice() throws IOException {
        changeSceneAdmin("Invoices_Admin.fxml");
    }

    public void showStatistics() throws IOException {
        changeSceneAdmin("Statistics_Admin.fxml");
    }

    public void goBack() throws IOException {
        if(showConfirmation("Bạn có muốn đăng xuất khỏi hệ thống ?")){
            LoggedInUser.logout();
            Main.changeScene("Login.fxml");
        }
    }
    public void changeSceneAdmin(String fxml) throws IOException {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent newContent = loader.load();

            contentAdmin.getChildren().clear();
            contentAdmin.getChildren().add(newContent);
        } catch (IOException e) {
            showError("Lỗi load danh sách", e.getMessage());
        }
    }
}
