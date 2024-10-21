module com.example.duanshopdienthoai {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens com.example.duanshopdienthoai to javafx.fxml;
    exports com.example.duanshopdienthoai;
    exports com.example.duanshopdienthoai.Login;
    opens com.example.duanshopdienthoai.Login to javafx.fxml;
}