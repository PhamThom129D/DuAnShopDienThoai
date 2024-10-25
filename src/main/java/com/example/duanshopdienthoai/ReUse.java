package com.example.duanshopdienthoai;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.util.Duration;

public class ReUse {
    public static void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(message);
        alert.show();
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                event -> alert.hide()
        ));
        timeline.setCycleCount(1);
        timeline.play();
    }
    public static boolean showConfirmation(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }
    public  static void showError(String message,String title ) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();

        Timeline timeline = new Timeline(new KeyFrame(
                Duration.millis(2000),
                event -> alert.hide()
        ));
        timeline.setCycleCount(1);
        timeline.play();
    }
}
