module com.example.duanshopdienthoai {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.duanshopdienthoai to javafx.fxml;
    exports com.example.duanshopdienthoai;
}